package me.viniciusarnhold.altaria.command.pool

import com.google.common.collect.ImmutableSet
import com.vdurmont.emoji.EmojiManager
import me.viniciusarnhold.altaria.command.CommandType
import me.viniciusarnhold.altaria.command.MessageUtils
import me.viniciusarnhold.altaria.command.Prefixes
import me.viniciusarnhold.altaria.command.UserPermissions
import me.viniciusarnhold.altaria.command.IMessageCommand
import me.viniciusarnhold.altaria.events.utils.Commands
import me.viniciusarnhold.altaria.utils.Actions
import me.viniciusarnhold.altaria.utils.TimeUtils
import me.viniciusarnhold.altaria.utils.Timers
import org.apache.commons.lang3.exception.ExceptionUtils
import org.apache.logging.log4j.LogManager
import sx.blah.discord.api.events.IListener
import sx.blah.discord.handle.impl.events.MessageReceivedEvent
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.util.DiscordException
import sx.blah.discord.util.MissingPermissionsException
import sx.blah.discord.util.RateLimitException
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * Created by Vinicius.

 * @since ${PROJECT_VERSION}
 */
class PoolCommand : IMessageCommand, IListener<MessageReceivedEvent> {
    init {
        logger.traceEntry()
        logger.traceExit()
    }

    private fun isPoolMessage(message: IMessage): Boolean {
        return !message.channel.isPrivate && MessageUtils.isMyCommand(message, this)
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

    /**
     * Called when the event is sent.

     * @param event The event object.
     */
    override fun handle(event: MessageReceivedEvent) {
        val message = event.message
        logger.traceEntry("Received command by {} with message {}", { message.author }, { message.content })

        try {
            if (!isPoolMessage(message)) return

            val args = Commands.splitByWhitespace(message.content.trim { it <= ' ' })
            if (args.size < 4 || args.size > 12) {
                Timers.MessageDeletionService.schedule(MessageUtils.getSimpleMentionMessage(message)
                        .appendContent(if (args.size > 37) "Too many options, max: 12 " else "")
                        .appendContent("Usage: ")
                        .appendContent(Prefixes.current())
                        .appendContent(command)
                        .appendContent("\"time\" \"Option1\" \"Option2\" ...")
                        .appendContent(System.lineSeparator())
                        .appendContent("See help for more examples.")
                        .send())

                return
            }

            val time: Int?
            try {
                time = Integer.parseInt(args[1])
            } catch (nfe: NumberFormatException) {
                logger.trace("Received Pool command but couldnt parse time argument, arg {} ex {}", { args[1] }, { ExceptionUtils.getStackTrace(nfe) })
                Timers.MessageDeletionService.schedule(MessageUtils.getSimpleMentionMessage(message)
                        .withContent("Second argument must be a number.")
                        .send())
                return
            }

            val options = LinkedHashMap<String, String>()
            var count = 0
            for (i in 2..args.size - 1) {
                options.put(LIST_EMOJIS[count++], args[i])
            }

            val builder = MessageUtils.getEmbedBuilder(message.author)
                    .withTitle("Pool Created")
                    .withDescription("This pool will last " + TimeUtils.formatToString(time.toLong(), TimeUnit.SECONDS)
                            + System.lineSeparator()
                            + "React to this message with the given Emojis to vote")

            for ((key, value) in options) {
                builder.appendField(value, String.format(POOL_LIST_FORMAT, key, value), false)
            }

            val poolMessage = MessageUtils.getMessageBuilder(message)
                    .withEmbed(builder.build())
                    .withTTS()
                    .send()

            val requestBuilder = MessageUtils.getDefaultRequestBuilder(message)
                    .doAction(Actions.ofSuccess())

            for (emoji in options.keys) {
                requestBuilder.andThen(Actions.ofSuccess {
                    poolMessage.addReaction(
                            EmojiManager.getForAlias(emoji)
                                    .unicode)
                }
                )
            }
            requestBuilder.andThen(Actions.ofSuccess({ message.delete() }))

            PoolManager.instance
                    .registerPool(message,
                            Pool(options,
                                    poolMessage,
                                    time.toLong(),
                                    if ("MultPool".equals(args[0], ignoreCase = true) || "MPool".equals(args[0], ignoreCase = true))
                                        Pool.Type.MULTI
                                    else
                                        Pool.Type.SINGLE))

            requestBuilder.execute()

        } catch (e: RateLimitException) {
            MessageUtils.handleDiscord4JException(logger, e, this, message)
        } catch (e: MissingPermissionsException) {
            MessageUtils.handleDiscord4JException(logger, e, this, message)
        } catch (e: DiscordException) {
            MessageUtils.handleDiscord4JException(logger, e, this, message)
        }

    }

    companion object {

        private val logger = LogManager.getLogger()

        val instance = PoolCommand()

        private val POOL_LIST_FORMAT = "%1s - %2s"

        private val command = "Pool"

        private val alias = ImmutableSet.of("MultPool", "SinglePool", "MPool")

        private val desc = "Creates a timed pool with the given choices, then posts back the results"

        private val type = me.viniciusarnhold.altaria.command.CommandType.UTIL

        private val permissions = EnumSet.of(UserPermissions.MANAGE_POOL)

        private val LIST_EMOJIS = Arrays.asList(
                ":one:",
                ":two:",
                ":three:",
                ":four:",
                ":five:",
                ":six:",
                ":seven:",
                ":eight:",
                ":nine:",
                ":keycap_ten:",
                ":regional_indicator_a:",
                ":regional_indicator_b:",
                ":regional_indicator_c:",
                ":regional_indicator_d:",
                ":regional_indicator_e:",
                ":regional_indicator_f:",
                ":regional_indicator_g:",
                ":regional_indicator_h:",
                ":regional_indicator_i:",
                ":regional_indicator_j:",
                ":regional_indicator_k:",
                ":regional_indicator_j:",
                ":regional_indicator_m:",
                ":regional_indicator_n:",
                ":regional_indicator_o:",
                ":regional_indicator_p:",
                ":regional_indicator_q:",
                ":regional_indicator_r:",
                ":regional_indicator_s:",
                ":regional_indicator_t:",
                ":regional_indicator_u:",
                ":regional_indicator_v:",
                ":regional_indicator_w:",
                ":regional_indicator_x:",
                ":regional_indicator_y:",
                ":regional_indicator_z:")
    }
}
