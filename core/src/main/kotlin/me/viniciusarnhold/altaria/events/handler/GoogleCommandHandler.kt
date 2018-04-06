package me.viniciusarnhold.altaria.events.handler

import com.google.api.services.urlshortener.model.Url
import me.viniciusarnhold.altaria.apis.google.GoogleClientServiceFactory
import me.viniciusarnhold.altaria.events.EventManager
import me.viniciusarnhold.altaria.events.interfaces.ICommandHandler
import me.viniciusarnhold.altaria.events.utils.Commands
import me.viniciusarnhold.altaria.events.utils.EventUtils
import org.apache.commons.cli.Option
import org.apache.commons.cli.Options
import org.apache.commons.cli.ParseException
import org.apache.commons.lang3.StringUtils
import org.apache.commons.validator.routines.UrlValidator
import org.apache.http.message.BasicHeader
import org.apache.logging.log4j.LogManager
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.util.DiscordException
import sx.blah.discord.util.MessageBuilder
import sx.blah.discord.util.MessageBuilder.Styles
import sx.blah.discord.util.MissingPermissionsException
import sx.blah.discord.util.RateLimitException
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * Created by Vinicius.

 * @since 1.0
 */
class GoogleCommandHandler private constructor() : ICommandHandler {

    @Throws(RateLimitException::class, DiscordException::class, MissingPermissionsException::class)
    override fun handle(event: MessageReceivedEvent, command: String, matchedText: String): Boolean {

        if (GOOGLE_SEARCH.mainCommand().equals(command, ignoreCase = true)) {
            try {
                val cmd = GOOGLE_SEARCH.parse(matchedText)

                if (GOOGLE_SEARCH.showHelpIfPresent(event.client, event.message.channel, cmd)) {
                    return true
                }
                val query = StringUtils.join(cmd.getOptionValues("t"), StringUtils.SPACE)

                val limit = Integer.parseInt(cmd.getOptionValue("l", "3"))

                makeGoogleSearch(event, query, limit)

            } catch (e: ParseException) {
                logger.info("Parsing failed", e)
                EventUtils.sendIncorrectUsageMessage(event.client, event.message.channel, GOOGLE_SEARCH.mainCommand(), e.message ?: "Unknown")
            }

            return true
        }

        if (SHORTEN_URL_COMMAND.mainCommand().equals(command, ignoreCase = true)) {
            try {
                val cmd = SHORTEN_URL_COMMAND.parse(matchedText)

                if (SHORTEN_URL_COMMAND.showHelpIfPresent(event.client, event.message.channel, cmd)) {
                    return true
                }

                shortenURL(event, cmd.getOptionValue("t"))

            } catch (e: ParseException) {
                logger.info("Parsing failed", e)
                EventUtils.sendIncorrectUsageMessage(event.client, event.message.channel, GOOGLE_SEARCH.mainCommand(), e.message ?: "Unknown")
            }

            return true
        }

        return false
    }

    @Throws(RateLimitException::class, DiscordException::class, MissingPermissionsException::class)
    private fun shortenURL(event: MessageReceivedEvent, url: String) {
        if (!UrlValidator.getInstance().isValid(url)) {
            EventUtils.sendIncorrectUsageMessage(event.client, event.message.channel, "ShortUrl", "URL is not a valid http/https URL!")
            return
        }
        try {
            val shortUrl = GoogleClientServiceFactory.urlShortenerService
                    .url()
                    .insert(Url().setLongUrl(url))
                    .execute()

            val builder = StringBuilder(StringUtils.defaultIfEmpty(shortUrl.id, "URL was removed by google."))

            if (shortUrl.status != null && !"OK".equals(shortUrl.status, ignoreCase = true)) {
                builder.append("\n")
                        .append("Attention:")
                        .append("   ")
                        .append(shortUrl.status)
            }

            MessageBuilder(event.client)
                    .withChannel(event.message.channel)
                    .withContent("Short URL: ")
                    .appendContent(builder.toString(), Styles.UNDERLINE)
                    .appendContent("\n")
                    .appendContent("Full URL: ")
                    .appendContent(url, Styles.UNDERLINE)
                    .send()

        } catch (e: IOException) {
            logger.error("I/O Error during call to URLShortening service", e)
            MessageBuilder(event.client)
                    .withChannel(event.message.channel)
                    .withContent("Fail during google URL Shortening API Contact.")
                    .send()
        }

    }

    override val handableCommands: List<Commands>
        get() = listOf<Commands>(GOOGLE_SEARCH)

    companion object {

        private val logger = LogManager.getLogger()

        //Google shortnen URL
        private val SHORTNEN_HEADER = BasicHeader("Content-Type", "application/json")
        private val SHORTNEN_API_URL = "https://www.googleapis.com/urlshortener/v1/url"
        private val LONG_URL_PARAM = "longUrl"

        //Google search
        private val BOT_USER_AGENT = "Mozilla/5.0 (Windows; U; Windows NT 6.1; rv:2.2) Gecko/20110201"
        private val GOOGLE_SEARCH_LINK = "http://www.google.com/search?q="

        private val GOOGLE_SEARCH: Commands
        private val SHORTEN_URL_COMMAND: Commands

        val instance = GoogleCommandHandler()

        init {
            var options = Options()
            options.addOption(Option.builder("l")
                    .longOpt("limit")
                    .hasArg()
                    .required(false)
                    .type(Int::class.java)
                    .desc("Limits the number of search results, max is five, if not specified 3.")
                    .argName("results")
                    .build())
                    .addOption(Option.builder("t")
                            .argName("search")
                            .longOpt("text")
                            .required()
                            .hasArgs()
                            .type(String::class.java)
                            .desc("The text to use as search argument.")
                            .build())
            GOOGLE_SEARCH = Commands.of("Search::Google", options, instance, "Searchs some text on google and posts back with the results.")

            options = Options()
            options.addOption(Option.builder("t")
                    .type(String::class.java)
                    .longOpt("text")
                    .desc("The text to be shortened")
                    .required()
                    .hasArg()
                    .argName("url")
                    .build())
            SHORTEN_URL_COMMAND = Commands.of("ShortUrl", options, instance, "Shortens a URL using Google URL Shortener.")
        }

        @Throws(DiscordException::class, MissingPermissionsException::class, RateLimitException::class)
        fun makeGoogleSearch(event: MessageReceivedEvent, search: String, limit: Int) {

            // The request also includes the userip parameter which provides the end
            // user's IP address. Doing so will help distinguish this legitimate
            // server-side traffic from traffic which doesn't come from an end-user.

            val links: Elements
            try {
                links = Jsoup.connect(GOOGLE_SEARCH_LINK + URLEncoder.encode(search, StandardCharsets.UTF_8.name()))
                        .userAgent(BOT_USER_AGENT)
                        .get()
                        .select(".g>.r>a")
            } catch (e: IOException) {
                EventUtils.sendConnectionErrorMessage(event.client, event.message.channel, "Search::Google", "Google Request Limit exceeded.", httpe = e as HttpStatusException)
                logger.error("Failed to connect to google during google search", e)
                return
            }

            val builder = MessageBuilder(EventManager.discordClient)
                    .withChannel(event.message.channel)
            var count = 0
            for (link in links) {
                if (count == limit) {
                    break
                }
                val title = link.text()
                var url = link.absUrl("href") // Google returns URLs in format "http://www.google.com/url?q=<url>&sa=U&ei=<someKey>".
                try {
                    url = URLDecoder.decode(url.substring(url.indexOf('=') + 1, url.indexOf('&')), "UTF-8")
                } catch (e: UnsupportedEncodingException) {
                    logger.error("Error during decoding Google Search URL Decoding", e)
                    return
                }

                if (!url.startsWith("http")) {
                    continue // Ads/news/etc.
                }

                builder.appendContent(title + ": ")
                builder.appendContent(url, Styles.ITALICS)
                builder.appendContent("\n")
                count++
            }
            builder.send()
        }
    }

}
