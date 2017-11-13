package me.viniciusarnhold.altaria.command.common

import me.viniciusarnhold.altaria.command.AbstractMessageCommand
import me.viniciusarnhold.altaria.command.CommandType
import me.viniciusarnhold.altaria.command.MessageUtils
import me.viniciusarnhold.altaria.command.UserPermissions
import me.viniciusarnhold.altaria.utils.Actions
import me.viniciusarnhold.altaria.utils.TimeUtils
import org.apache.logging.log4j.LogManager
import sx.blah.discord.handle.impl.events.MessageReceivedEvent
import sx.blah.discord.util.DiscordException
import sx.blah.discord.util.MissingPermissionsException
import sx.blah.discord.util.RateLimitException
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by Vinicius.

 * @since ${PROJECT_VERSION}
 */
class PingCommand : AbstractMessageCommand() {
    init {
        this.command = "ping"
        this.aliases = emptySet<String>()
        this.commandType = me.viniciusarnhold.altaria.command.CommandType.BOT
        this.description = "Measures the time a command take to reach the bot"
        this.permissions = EnumSet.noneOf<UserPermissions>(UserPermissions::class.java)
    }

    /**
     * Called when the event is sent.

     * @param event The event object.
     */
    override fun handle(event: MessageReceivedEvent) {
        if (!isPingCommand(event)) {
            return
        }
        logger.traceEntry("Received ping command. {}", event)

        try {
            MessageUtils.getDefaultRequestBuilder(event.message)
                    .doAction(Actions.ofSuccess {
                        MessageUtils.getMessageBuilder(event.message)
                                .appendContent("Pong!")
                                .appendContent(System.lineSeparator())
                                .appendContent("Last reponse time in: ")
                                .appendContent(TimeUtils.formatToString(event.message.shard.responseTime, TimeUnit.MILLISECONDS))
                                .send()
                    })
                    .andThen(Actions.ofSuccess({ event.message.delete() }))
                    .execute()

        } catch (e: RateLimitException) {
            logger.error(e)
        } catch (e: MissingPermissionsException) {
            logger.error(e)
        } catch (e: DiscordException) {
            logger.error(e)
        }

    }

    private fun isPingCommand(event: MessageReceivedEvent): Boolean {
        return !event.message.channel.isPrivate &&
                !event.message.author.isBot &&
                MessageUtils.isMyCommand(event.message, this)
    }

    companion object {

        private val logger = LogManager.getLogger()
    }
}
