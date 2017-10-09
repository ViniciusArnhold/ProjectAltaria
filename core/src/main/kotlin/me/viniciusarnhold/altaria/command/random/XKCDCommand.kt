package me.viniciusarnhold.altaria.command.random

import com.diffplug.common.base.Errors
import com.fasterxml.jackson.databind.ObjectMapper
import me.viniciusarnhold.altaria.apis.HttpManager
import me.viniciusarnhold.altaria.apis.objects.XKCDComic
import me.viniciusarnhold.altaria.command.AbstractCommand
import me.viniciusarnhold.altaria.command.CommandType
import me.viniciusarnhold.altaria.command.MessageUtils
import me.viniciusarnhold.altaria.command.UserPermissions
import me.viniciusarnhold.altaria.events.utils.Commands
import me.viniciusarnhold.altaria.utils.Actions
import okhttp3.Request
import org.apache.commons.lang3.StringUtils
import org.apache.logging.log4j.LogManager
import sx.blah.discord.handle.impl.events.MessageReceivedEvent
import sx.blah.discord.util.DiscordException
import sx.blah.discord.util.MissingPermissionsException
import sx.blah.discord.util.RateLimitException
import java.io.IOException
import java.util.*
import java.util.Collections.emptySet
import java.util.concurrent.ThreadLocalRandom

/**
 * Created by Vinicius.

 * @since ${PROJECT_VERSION}
 */
class XKCDCommand : AbstractCommand() {
    init {
        this.command = "xkcd"
        this.aliases = emptySet<String>()
        this.commandType = CommandType.GENERAL
        this.description = "Posts links of XKCD.com, use with xkcd <num>, xkcd random, xkcd latest"
        this.permissions = EnumSet.noneOf<UserPermissions>(UserPermissions::class.java)
    }

    /**
     * Called when the event is sent.

     * @param event The event object.
     */
    override fun handle(event: MessageReceivedEvent) {
        if (!isXKCDCommand(event)) {
            return
        }
        try {
            logger.traceEntry("Received XKCD Command {}", event.message.content)
            val args = Commands.splitByWhitespace(event.message.content)
            val requestBuilder = MessageUtils.getDefaultRequestBuilder(event.message)
                    .doAction(Actions.ofSuccess())

            if (args.size < 2 || "latest".equals(args[1], ignoreCase = true)) {

                requestBuilder.andThen(getComic(XKCD_LATEST_URL, event))

            } else if ("random".equals(args[1], ignoreCase = true)) {

                val latest = latestComic
                val num: Int
                if (latest != null) {
                    num = ThreadLocalRandom.current().nextInt(1, latest.comicNumber + 1)
                } else {
                    num = ThreadLocalRandom.current().nextInt(1, 1800)
                }

                requestBuilder.andThen(getComic(String.format(XKCD_NUMBERED_URL, num), event))

            } else if (StringUtils.isNumericSpace(args[1])) {

                requestBuilder.andThen(getComic(String.format(XKCD_NUMBERED_URL, Integer.parseUnsignedInt(args[1])), event))

            } else {
                requestBuilder.andThen(Actions.ofSuccess {
                    MessageUtils.getSimpleMentionMessage(event.message)
                            .appendContent("Unknown args { " + args[1] + " }")
                            .appendContent(System.lineSeparator())
                            .appendContent(this.description)
                            .send()
                })
            }
            requestBuilder
                    .andThen(Actions.ofSuccess({ event.message.delete() }))
                    .execute()

        } catch (e: Exception) {
            logger.error("Failed to handle command", e)
        }

    }

    private val latestComic: XKCDComic?
        get() {
            val request = Request.Builder()
                    .url("http://xkcd.com/info.0.json")
                    .build()

            try {
                HttpManager.instance.defaultClient.newCall(request).execute().use({ response ->

                    if (response.isSuccessful) {

                        val mapper = ObjectMapper()

                        return mapper.readValue<XKCDComic>(response.body().string(), XKCDComic::class.java)
                    }
                })
            } catch (e: IOException) {
                logger.error(e)
            }

            return null
        }

    @Throws(RateLimitException::class, DiscordException::class, MissingPermissionsException::class)
    private fun getComic(url: String, event: MessageReceivedEvent): () -> Boolean {

        val request = Request.Builder()
                .url(url)
                .build()

        try {
            HttpManager.instance.defaultClient.newCall(request).execute().use({ response ->

                if (response.isSuccessful) {

                    val mapper = ObjectMapper()

                    val comic = mapper.readValue<XKCDComic>(response.body().string(), XKCDComic::class.java)

                    return Actions.ofSuccess {
                        MessageUtils.getMessageBuilder(event.message)
                                .withEmbed(
                                        MessageUtils.getEmbedBuilder(event.message.author)
                                                .ignoreNullEmptyFields()
                                                .withTitle("${comic.comicNumber} : ${comic.title}")
                                                .withDescription("Posted on: ${comic.day}/${comic.month}/${comic.year}")
                                                .appendField("Alt Text", comic.altText, false)
                                                .withImage(comic.imageLink)
                                                .build())
                                .send()
                    }


                } else {
                    if (response.code() == 404) {
                        return Actions.ofSuccess {
                            MessageUtils.getMessageBuilder(event.message)
                                    .appendContent("Commic not found")
                                    .send()
                        }
                    } else {
                        return Actions.ofSuccess {
                            Errors.log().wrap {
                                MessageUtils.getMessageBuilder(event.message)
                                        .appendContent("Failed to contact XKCD.com, ")
                                        .appendContent("Http Error: ")
                                        .appendContent(Integer.toString(response.code()))
                                        .appendContent(System.lineSeparator())
                                        .appendContent(response.body().string())
                                        .send()
                            }
                        }
                    }
                }
            })
        } catch (e: IOException) {
            logger.error(e)
            return Actions.ofSuccess { MessageUtils.getMessageBuilder(event.message).appendContent(e.message).send() }
        }

    }

    private fun isXKCDCommand(event: MessageReceivedEvent): Boolean {
        return !event.message.channel.isPrivate &&
                !event.message.author.isBot &&
                MessageUtils.isMyCommand(event.message, this)
    }

    companion object {

        private val logger = LogManager.getLogger()

        private val XKCD_NUMBERED_URL = "https://xkcd.com/%1s/info.0.json"

        private val XKCD_LATEST_URL = "https://xkcd.com/info.0.json"
    }
}
