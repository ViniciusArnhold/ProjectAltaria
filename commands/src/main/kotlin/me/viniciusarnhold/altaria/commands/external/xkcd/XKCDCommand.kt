package me.viniciusarnhold.altaria.commands.external.xkcd

import com.fasterxml.jackson.annotation.JsonProperty
import me.viniciusarnhold.altaria.api.LongArg
import me.viniciusarnhold.altaria.api.MessageCommandContext
import me.viniciusarnhold.altaria.apis.HttpManagerService
import me.viniciusarnhold.altaria.apis.service.JacksonService
import okhttp3.Request
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.util.RequestBuffer
import java.net.URL
import java.util.concurrent.ThreadLocalRandom

/**
 * Created by Vinicius.

 * @since 1.0
 */
class XKCDCommand : me.viniciusarnhold.altaria.api.AbstractMessageCommand() {
    override val command = "xkcd"
    override val description = "Posts links of XKCD.com, use with xkcd <num>, xkcd random, xkcd latest"

    override fun onCommand(ctx: MessageCommandContext) {
        if (ctx.isFromBot() || ctx.isPrivate()) return

        val args = ctx.command.args
        if (args.size > 0) {
            return
        }

        val firstArg = args.firstOrNull()
        when {
            firstArg == null || args.size > 1 -> ctx.replyWithError("Unknown args", true)
            firstArg is LongArg -> replyWithNumberedComic(firstArg.longValue, ctx)
            firstArg.value.equals("latest", true) -> replyWithLatestComic(ctx)
            firstArg.value.equals("random", true) -> replyWithRandomComic(ctx)
            else -> ctx.replyWithError("Unknown args", true)
        }

        ctx.enqueue { ctx.event.message.delete() }
    }

    private fun replyWithRandomComic(ctx: MessageCommandContext) {
        val latestComic = getComicByURL(XKCD_LATEST_URL, ctx)
        when (latestComic) {
            null -> ctx.replyWithError("Error when retrieving latest comic")
            else -> {
                val comicNumber = ThreadLocalRandom.current().nextInt(1, latestComic.comicNumber + 1)
                val randomComic = getComicByURL(XKCD_NUMBERED_URL.format(comicNumber), ctx)
                when (randomComic) {
                    null -> ctx.replyWithError("Error when retrieving comic ${comicNumber}")
                    else -> replyForComic(ctx, randomComic)
                }
            }
        }
    }

    private fun replyWithLatestComic(ctx: MessageCommandContext): RequestBuffer.RequestFuture<IMessage> {
        val latestComic = getComicByURL(XKCD_LATEST_URL, ctx)
        return when (latestComic) {
            null -> ctx.replyWithError("Error when retrieving latest comic")
            else -> replyForComic(ctx, latestComic)
        }
    }

    private fun replyWithNumberedComic(comicNumber: Long, ctx: MessageCommandContext) {
        val comic = getComicByURL(XKCD_NUMBERED_URL.format(comicNumber), ctx)
        when (comic) {
            null -> ctx.replyWithError("Error when retrieving comic ${comicNumber}")
            else -> replyForComic(ctx, comic)
        }
    }


    private fun replyForComic(ctx: MessageCommandContext, comic: XKCDComic): RequestBuffer.RequestFuture<IMessage> {
        val date = "${comic.releaseYear}/${comic.releaseMonth}/${comic.releaseDay}"
        return ctx.reply(ctx.withEmbed()
                .withTitle("${comic.comicNumber} - ${comic.title}")
                .withDescription("Posted on: ${date}")
                .appendField("Alt Text", comic.altTest, false)
                .withImage(comic.imagelink.toString()))
    }

    private fun getComicByURL(url: String, ctx: MessageCommandContext): XKCDComic? {
        val request = Request.Builder()
                .url(url)
                .build()

        return ctx.configuration.getService(HttpManagerService::class.java).defaultClient.newCall(request)
                .execute().use { response ->
                    when {
                        response.isSuccessful -> ctx.configuration.getService(JacksonService::class.java)
                                .mapper.readValue(response.body().charStream(), XKCDComic::class.java)
                        else -> null
                    }
                }
    }

    companion object {
        private val XKCD_NUMBERED_URL = "https://xkcd.com/%d/info.0.json"

        private val XKCD_LATEST_URL = "https://xkcd.com/info.0.json"
    }
}

/**
 * Created by Vinicius.

 * @since 1.0
 */
class XKCDComic(
        @JsonProperty("day") val releaseDay: Int,
        @JsonProperty("month") val releaseMonth: Int,
        @JsonProperty("year") val releaseYear: Int,
        @JsonProperty("num") val comicNumber: Int,
        @JsonProperty("link") val comicLink: URL,
        @JsonProperty("news") val news: String,
        @JsonProperty("title") val title: String,
        @JsonProperty("safe_title") val safeTitle: String,
        @JsonProperty("transcript") val transcript: String,
        @JsonProperty("alt") val altTest: String,
        @JsonProperty("img") val imagelink: URL
)