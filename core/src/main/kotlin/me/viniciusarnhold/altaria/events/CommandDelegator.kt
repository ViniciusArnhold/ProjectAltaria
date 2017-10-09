package me.viniciusarnhold.altaria.events

import me.viniciusarnhold.altaria.events.handler.BotInfoCommandHandler
import me.viniciusarnhold.altaria.events.handler.GoogleCommandHandler
import me.viniciusarnhold.altaria.events.handler.SimpleCommandHandler
import me.viniciusarnhold.altaria.events.interfaces.ICommandHandler
import me.viniciusarnhold.altaria.events.utils.Commands
import org.apache.logging.log4j.LogManager
import sx.blah.discord.handle.impl.events.MessageReceivedEvent
import java.util.*
import java.util.concurrent.ConcurrentSkipListSet

/**
 * Created by Vinicius.

 * @since ${PROJECT_VERSION}
 */
class CommandDelegator private constructor() {

    fun degelateCommand(event: MessageReceivedEvent, command: String, matchedText: String): Boolean {
        val entryMessage = logger.traceEntry("CommandDelegator will delegate command {}.", matchedText)

        var handleCount = 0
        for (handler in HANDLERS) {
            try {
                if (handler.handle(event, command, matchedText)) {
                    handleCount++
                }
            } catch (e: Exception) {
                logger.error("Handler " + handler.javaClass.simpleName + " failed to handle command.", e)
            }

        }
        return logger.traceExit(entryMessage, handleCount > 0)
    }

    companion object {

        val logger = LogManager.getLogger()

        private val COMMANDS = ConcurrentSkipListSet<Commands>()

        private val HANDLERS = ConcurrentSkipListSet(Comparator.comparing<ICommandHandler, String> { o -> o.javaClass.name })
        val instance = CommandDelegator()

        init {
            //Register HANDLERS
            Collections.addAll(HANDLERS, BotInfoCommandHandler.instance, SimpleCommandHandler.instance, GoogleCommandHandler.instance)

            //Register Commands
            HANDLERS.forEach { handler -> COMMANDS.addAll(handler.handableCommands!!) }
        }

        val allCommands: Set<Commands>
            get() = Collections.unmodifiableSet(COMMANDS)
    }

}
