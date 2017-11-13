package me.viniciusarnhold.altaria.command.common

import me.viniciusarnhold.altaria.command.AbstractMessageCommand
import me.viniciusarnhold.altaria.command.CommandType
import me.viniciusarnhold.altaria.command.MessageUtils
import me.viniciusarnhold.altaria.command.UserPermissions
import me.viniciusarnhold.altaria.utils.Actions
import me.viniciusarnhold.altaria.utils.TimeUtils
import org.apache.logging.log4j.LogManager
import sx.blah.discord.handle.impl.events.MessageReceivedEvent
import java.util.*

/**
 * Created by Vinicius.

 * @since ${PROJECT_VERSION}
 */
class UptimeCommand : AbstractMessageCommand() {
    init {
        this.command = "uptime"
        this.aliases = emptySet<String>()
        this.commandType = me.viniciusarnhold.altaria.command.CommandType.BOT
        this.description = "Measures the time this bot has been onnline"
        this.permissions = EnumSet.noneOf<UserPermissions>(UserPermissions::class.java)
    }

    /**
     * Called when the event is sent.

     * @param event The event object.
     */
    override fun handle(event: MessageReceivedEvent) {
        if (!isUptimeCommand(event)) {
            return
        }
        logger.traceEntry("Received uptime command {}", event)

        try {
            MessageUtils.getDefaultRequestBuilder(event.message)
                    .doAction(Actions.ofSuccess {
                        MessageUtils.getMessageBuilder(event.message)
                                .appendContent("This bot has been online for: ")
                                .appendContent(TimeUtils.formatAsElapsed())
                                .send()
                    })
                    .andThen(Actions.ofSuccess({ event.message.delete() }))
                    .execute()

        } catch (e: Exception) {
            logger.error(e)
        }

    }

    private fun isUptimeCommand(event: MessageReceivedEvent): Boolean {
        return !event.message.channel.isPrivate &&
                !event.message.author.isBot &&
                MessageUtils.isMyCommand(event.message, this)
    }

    companion object {

        private val logger = LogManager.getLogger()
    }
}
