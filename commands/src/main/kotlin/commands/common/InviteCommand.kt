package me.viniciusarnhold.altaria.command.common

import com.google.common.collect.ImmutableSet
import me.viniciusarnhold.altaria.command.CommandType
import me.viniciusarnhold.altaria.command.MessageUtils
import me.viniciusarnhold.altaria.command.UserPermissions
import me.viniciusarnhold.altaria.command.IMessageCommand
import me.viniciusarnhold.altaria.core.BotManager
import me.viniciusarnhold.altaria.events.utils.Commands
import me.viniciusarnhold.altaria.utils.Actions
import org.apache.logging.log4j.LogManager
import sx.blah.discord.api.events.IListener
import sx.blah.discord.handle.impl.events.MessageReceivedEvent
import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.util.DiscordException
import sx.blah.discord.util.RateLimitException
import java.util.*

/**
 * Created by Vinicius.

 * @since ${PROJECT_VERSION}
 */
class InviteCommand : IListener<MessageReceivedEvent>, IMessageCommand {

    /**
     * Called when the event is sent.

     * @param event The event object.
     */
    override fun handle(event: MessageReceivedEvent) {
        if (!isInviteCommand(event)) {
            return
        }
        try {

            logger.traceEntry("Received Invite command with {}", { event.message.content })

            val args = Commands.splitByWhitespace(event.message.content.trim { it <= ' ' })

            var isLocal = false
            if (args.size > 1 && "here" == args[1]) {
                isLocal = true
            }

            val channelToSend: IChannel
            if (!isLocal) {
                var channel: IChannel
                try {
                    channel = event.message.author.orCreatePMChannel
                } catch (de: DiscordException) {
                    logger.error(de)
                    channel = event.message.channel
                } catch (de: RateLimitException) {
                    logger.error(de)
                    channel = event.message.channel
                }

                channelToSend = channel
            } else {
                channelToSend = event.message.channel
            }

            val builder = MessageUtils.getMessageBuilder(event.message)
                    .withChannel(channelToSend)
                    .appendContent("Here's my invite link!")
                    .appendContent(System.lineSeparator())
                    .appendContent(BotManager.instance.inviteUrl())

            MessageUtils.getDefaultRequestBuilder(event.message)
                    .doAction(Actions.ofSuccess({ builder.send() }))
                    .andThen(Actions.ofSuccess({ event.message.delete() }))
                    .execute()

        } catch (e: Exception) {
            logger.error("Failed to handle Invite command", e)
        }

    }

    private fun isInviteCommand(event: MessageReceivedEvent): Boolean {
        return !event.message.channel.isPrivate &&
                !event.message.author.isBot &&
                MessageUtils.isMyCommand(event.message, this)
    }

    override fun command(): String {
        return command
    }

    override fun aliases(): Set<String> {
        return alias
    }

    override fun description(): String {
        return desc
    }

    override fun type(): me.viniciusarnhold.altaria.command.CommandType {
        return type
    }

    override fun permissions(): EnumSet<UserPermissions> {
        return permissions
    }

    companion object {

        private val logger = LogManager.getLogger()

        private val command = "Invite"

        private val alias = ImmutableSet.of("Inv", "Invitation")

        private val desc = "Returns the invite link for this bot."

        private val type = me.viniciusarnhold.altaria.command.CommandType.BOT

        private val permissions = EnumSet.noneOf<UserPermissions>(UserPermissions::class.java)
    }
}
