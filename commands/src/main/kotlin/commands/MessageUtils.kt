package me.viniciusarnhold.altaria.command

import me.viniciusarnhold.altaria.core.BotManager
import me.viniciusarnhold.altaria.utils.Logs
import me.viniciusarnhold.altaria.utils.Timers
import org.apache.commons.lang3.StringUtils
import org.apache.logging.log4j.Logger
import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.handle.obj.IDiscordObject
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.handle.obj.IUser
import sx.blah.discord.util.*
import java.awt.Color
import java.time.LocalDateTime
import java.util.function.Supplier

/**
 * Created by Vinicius.

 * @since ${PROJECT_VERSION}
 */
object MessageUtils {

    private val ICON_URL = "https://vignette2.wikia.nocookie.net/pokemon--shuffle/images/e/e2/334.png/revision/latest?cb=20150912015748"


    fun isMyCommand(message: IMessage, thiz: IMessageCommand): Boolean {
        return isMyCommand(message.content, thiz)
    }

    fun isMyCommand(message: String, thiz: IMessageCommand): Boolean {
        var message = message
        message = message.trim { it <= ' ' }
        val prefix = Prefixes.current()
        if (!message.startsWith(prefix)) {
            return false
        }
        if (StringUtils.startsWithIgnoreCase(message, prefix + thiz.command())) {
            return true
        }

        for (alias in thiz.aliases()) {
            if (StringUtils.startsWithIgnoreCase(message, prefix + alias)) {
                return true
            }
        }
        return false
    }

    val embedBuilder: EmbedBuilder
        get() = EmbedBuilder()
                .withAuthorName(BotManager.BOT_NAME)
                .withAuthorUrl(BotManager.REPO_URL)
                .withColor(Color(36, 177, 222))
                .withAuthorIcon(ICON_URL)
                .withTimestamp(LocalDateTime.now())

    fun getEmbedBuilder(user: IUser): EmbedBuilder {
        return EmbedBuilder()
                .withAuthorName(BotManager.BOT_NAME)
                .withAuthorUrl(BotManager.REPO_URL)
                .withColor(Color(36, 177, 222))
                .withAuthorIcon(ICON_URL)
                .withFooterIcon(user.avatarURL)
                .withFooterText("Requested by @" + user.name)
                .withTimestamp(LocalDateTime.now())
    }

    fun getDefaultRequestBuilder(obj: IDiscordObject<*>): RequestBuilder {
        return RequestBuilder(obj.getClient())
                .shouldBufferRequests(true)
                .onTimeout { Logs.forMessage("Timeout while executing requests on default requestBuilder") }
                .setAsync(true)
                .shouldFailOnException(false)
    }

    fun getMessageBuilder(message: IMessage): MessageBuilder {
        return getMessageBuilder(message.channel)
    }

    fun getMessageBuilder(channel: IChannel): MessageBuilder {
        return MessageBuilder(channel.client)
                .withChannel(channel)
    }

    fun getSimpleMentionMessage(message: IMessage): MessageBuilder {
        return getMessageBuilder(message)
                .withContent(message.author.mention())
                .appendContent(" ")
    }

    fun <T> retry(supplier: Supplier<T>): T {
        var value: T? = null
        do {
            try {
                value = supplier.get()
            } catch (rle: Exception) {
                if (rle !is RateLimitException) {
                    BotManager.LOGGER.error(rle)
                    throw RuntimeException(rle)
                }
                try {
                    Thread.sleep(rle.retryDelay)
                } catch (e: InterruptedException) {
                    BotManager.LOGGER.error(e)
                }
            }
        } while (value == null)
        return value
    }

    fun handleDiscord4JException(logger: Logger, e: Exception) {
        logger.error(e)
    }

    fun <T> supress(supplier: Supplier<T>): T {
        try {
            return supplier.get()
        } catch (e: Exception) {
            BotManager.LOGGER.error(e)
            throw RuntimeException(e)
        }

    }

    fun handleDiscord4JException(logger: Logger, e: Exception, commandHandler: IMessageCommand, message: IMessage) {
        try {
            logger.error("{} failed to handle command, a {} was captured",
                    { commandHandler.javaClass.simpleName },
                    { e.javaClass.simpleName })
            logger.error(e)

            if (e is MissingPermissionsException) {
                Timers.MessageDeletionService.schedule(getMessageBuilder(message)
                        .appendContent(message.author.mention())
                        .appendContent(" I dont have the necessary permissions to execute that action,")
                        .appendContent(" please give me the following permissions and try again")
                        .appendContent(System.lineSeparator())
                        .appendContent(e.missingPermissions.toString())
                        .send())
            }

        } catch (e1: RateLimitException) {
            logger.error(e1)
        } catch (e1: DiscordException) {
            logger.error(e1)
        } catch (e1: MissingPermissionsException) {
            logger.error(e1)
        }

    }
}
