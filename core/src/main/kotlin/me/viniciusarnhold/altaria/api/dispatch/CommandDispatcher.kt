package me.viniciusarnhold.altaria.api.dispatch

import com.google.common.base.Stopwatch
import com.google.common.util.concurrent.AbstractService
import me.viniciusarnhold.altaria.api.*
import me.viniciusarnhold.altaria.events.StatsCounter
import org.apache.logging.log4j.LogManager
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import java.util.concurrent.*
import java.util.function.Supplier

/**
 * @author Vinicius Pegorini Arnhold.
 */
class CommandDispatcher(private val config: Configuration) : AbstractService() {
    companion object {
        private val LOGGER = LogManager.getLogger()
    }

    private var stopped: Boolean = false

    override fun doStop() {
        this.stopped = true
    }

    override fun doStart() {
        config.workPool.submit(this::run)
    }

    private fun run() {
        this.notifyStarted()
        try {
            while (!stopped) {
                val event: MessageReceivedEvent? = config.commandQueue.poll(10, TimeUnit.MILLISECONDS)
                event?.let {
                    LOGGER.info(Markers.DISPATCH, "Received MessageCommandEvent [ID: {}, Content: {}]", event::getMessageID, event.message::getContent)
                    CompletableFuture.supplyAsync(DispatchHandle(config, event), config.workPool)
                            .whenComplete({ r, e ->
                                when (e) {
                                    null -> when (r) {
                                        is DispatchHandle.Result.NotACommand -> LOGGER.trace(Markers.DISPATCH, "Command {} is not a command", event.messageID)
                                        is DispatchHandle.Result.NotMatched -> LOGGER.warn(Markers.DISPATCH, "Command {} not matched", event.messageID)
                                        is DispatchHandle.Result.Sucess -> LOGGER.trace(Markers.DISPATCH, "Command {} successfully handled by commands [{}]", event.messageID, r.handlers)
                                        is DispatchHandle.Result.Failure -> LOGGER.info(Markers.DISPATCH, "Command {} handling failure, successfully handled by [{}], errors [{}]",
                                                event.messageID, r.success, r.errors.map { (k, v) -> "${k}: [${v::class.simpleName}-> ${v.localizedMessage}]" })
                                    }
                                    else -> LOGGER.error(Markers.DISPATCH, "Failure in DispatchHandle Thread", e)
                                }
                            })
                }
            }
        } catch (e: Throwable) {
            notifyFailed(e)
            throw e
        } finally {
            notifyStopped()
        }
    }

    class DispatchHandle(private val config: Configuration, private val event: MessageReceivedEvent) : Supplier<DispatchHandle.Result> {
        override fun get(): Result {

            val cmdConfig = config.bot.configurationFor(event.guild)
            val parsedCommand = config.parser.parse(cmdConfig.prefix(), event.message.content)
            return when (parsedCommand) {
                null -> Result.NotACommand
                else -> {
                    val matches = config.registry.registeredCommands.asSequence().filter {
                        it.command.equals(parsedCommand.name, true)
                                || it.aliases.any { it.equals(parsedCommand.name, true) }
                    }
                    when {
                        matches.none() -> Result.NotMatched
                        else -> {
                            val sucess = mutableSetOf<IMessageCommand>()
                            val errors = mutableMapOf<IMessageCommand, Exception>()

                            for (handler in matches) {
                                try {
                                    handler.onCommand(MessageCommandContext(parsedCommand, event, cmdConfig, config.bot))
                                } catch (e: Exception) {
                                    val msg = "Handler ${handler} failed to handle command ${event.messageID}"
                                    val cmdException = CommandHandleException(msg, e)
                                    LOGGER.error(Markers.DISPATCH, msg, cmdException)
                                    errors.put(handler, e)
                                }

                            }
                            when {
                                errors.isEmpty() -> Result.Sucess(sucess)
                                else -> Result.Failure(sucess, errors)
                            }
                        }
                    }
                }
            }
        }

        sealed class Result {
            object NotMatched : Result()
            object NotACommand : Result()
            class Sucess(val handlers: Set<IMessageCommand>) : Result()
            class Failure(val success: Set<IMessageCommand>, val errors: Map<IMessageCommand, Exception>) : Result()
        }
    }

    internal class CommandExecutor(val command: IMessageCommand, val ctx: MessageCommandContext) {
        fun execute() {
            var sucess = true
            val stopwatch = Stopwatch.createStarted()
            try {
                command.onCommand(ctx)
            } catch (e: Exception) {
                LOGGER.error(Markers.DISPATCH, "Uncaught exception when executing command ${command}", e)
                sucess = false
            }
            if (sucess || ctx.bot.globalConfiguration.bot.command.stats.incrementWithError) {
                StatsCounter.addCommandHandled(1)
                StatsCounter.addCommandHandleTime(stopwatch)
            }
        }
    }

    class Configuration(internal val bot: AltariaBot, internal val registry: CommandRegistry, internal val parser: CommandParser, internal val workPool: ExecutorService,
                        internal val commandQueue: BlockingQueue<MessageReceivedEvent>)
}

class CommandRegistry {

    private val registry = CopyOnWriteArrayList<IMessageCommand>()

    val registeredCommands: List<IMessageCommand>
        get() = registry

    fun registerCommand(command: IMessageCommand) {
        registry.add(command)
    }

    fun unregisterCommand(command: IMessageCommand) {
        if (!registry.remove(command)) {
            throw IllegalStateException("Command ${command} not registered")
        }
    }
}
