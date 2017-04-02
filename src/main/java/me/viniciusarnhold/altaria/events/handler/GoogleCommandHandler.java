package me.viniciusarnhold.altaria.events.handler;

import com.google.api.services.urlshortener.model.Url;
import me.viniciusarnhold.altaria.apis.google.GoogleClientServiceFactory;
import me.viniciusarnhold.altaria.events.EventManager;
import me.viniciusarnhold.altaria.events.interfaces.ICommandHandler;
import me.viniciusarnhold.altaria.events.utils.Commands;
import me.viniciusarnhold.altaria.events.utils.EventUtils;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MessageBuilder.Styles;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

/**
 * Created by Vinicius.
 *
 * @since 1.0
 */
public class GoogleCommandHandler implements ICommandHandler {

    private static final Logger logger = LogManager.getLogger();

    //Google shortnen URL
    private static final Header SHORTNEN_HEADER = new BasicHeader("Content-Type", "application/json");
    private static final String SHORTNEN_API_URL = "https://www.googleapis.com/urlshortener/v1/url";
    private static final String LONG_URL_PARAM = "longUrl";

    //Google search
    private static final String BOT_USER_AGENT = "Mozilla/5.0 (Windows; U; Windows NT 6.1; rv:2.2) Gecko/20110201";
    private static final String GOOGLE_SEARCH_LINK = "http://www.google.com/search?q=";

    @NotNull
    private static final Commands GOOGLE_SEARCH;
    @NotNull
    private static final Commands SHORTEN_URL_COMMAND;

    @NotNull
    private static GoogleCommandHandler ourInstance = new GoogleCommandHandler();

    static {
        @NotNull Options options = new Options();
        options.addOption(Option.builder("l")
                                .longOpt("limit")
                                .hasArg()
                                .required(false)
                                .type(Integer.class)
                                .desc("Limits the number of search results, max is five, if not specified 3.")
                                .argName("results")
                                .build())
               .addOption(Option.builder("t")
                                .argName("search")
                                .longOpt("text")
                                .required()
                                .hasArgs()
                                .type(String.class)
                                .desc("The text to use as search argument.")
                                .build());
        GOOGLE_SEARCH = Commands.of("Search::Google", options, getInstance(), "Searchs some text on google and posts back with the results.");

        options = new Options();
        options.addOption(Option.builder("t")
                                .type(String.class)
                                .longOpt("text")
                                .desc("The text to be shortened")
                                .required()
                                .hasArg()
                                .argName("url")
                                .build());
        SHORTEN_URL_COMMAND = Commands.of("ShortUrl", options, getInstance(), "Shortens a URL using Google URL Shortener.");
    }

    private GoogleCommandHandler() {
    }

    @NotNull
    public static GoogleCommandHandler getInstance() {
        return ourInstance;
    }

    public static void makeGoogleSearch(@NotNull MessageReceivedEvent event, @NotNull String search, int limit) throws DiscordException, MissingPermissionsException, RateLimitException {

        // The request also includes the userip parameter which provides the end
        // user's IP address. Doing so will help distinguish this legitimate
        // server-side traffic from traffic which doesn't come from an end-user.

        Elements links;
        try {
            links = Jsoup.connect(GOOGLE_SEARCH_LINK + URLEncoder.encode(search, StandardCharsets.UTF_8.name()))
                         .userAgent(BOT_USER_AGENT)
                         .get()
                         .select(".g>.r>a");
        } catch (IOException e) {
            EventUtils.sendConnectionErrorMessage(event.getClient(), event.getMessage().getChannel(), "Search::Google", "Google Request Limit exceeded.", (HttpStatusException) e);
            logger.error("Failed to connect to google during google search", e);
            return;
        }

        MessageBuilder builder = new MessageBuilder(EventManager.getDiscordClient())
                .withChannel(event.getMessage().getChannel());
        int count = 0;
        for (@NotNull Element link : links) {
            if (count == limit) {
                break;
            }
            String title = link.text();
            String url = link.absUrl("href"); // Google returns URLs in format "http://www.google.com/url?q=<url>&sa=U&ei=<someKey>".
            try {
                url = URLDecoder.decode(url.substring(url.indexOf('=') + 1, url.indexOf('&')), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                logger.error("Error during decoding Google Search URL Decoding", e);
                return;
            }

            if (!url.startsWith("http")) {
                continue; // Ads/news/etc.
            }

            builder.appendContent(title + ": ");
            builder.appendContent(url, Styles.ITALICS);
            builder.appendContent("\n");
            count++;
        }
        builder.send();
    }

    @Override
    public boolean handle(@NotNull MessageReceivedEvent event, String command, @NotNull String matchedText) throws RateLimitException, DiscordException, MissingPermissionsException {

        if (GOOGLE_SEARCH.mainCommand().equalsIgnoreCase(command)) {
            try {
                CommandLine cmd = GOOGLE_SEARCH.parse(matchedText);

                if (GOOGLE_SEARCH.showHelpIfPresent(event.getClient(), event.getMessage().getChannel(), cmd)) {
                    return true;
                }
                String query = StringUtils.join(cmd.getOptionValues("t"), StringUtils.SPACE);

                int limit = Integer.parseInt(cmd.getOptionValue("l", "3"));

                makeGoogleSearch(event, query, limit);

            } catch (ParseException e) {
                logger.info("Parsing failed", e);
                EventUtils.sendIncorrectUsageMessage(event.getClient(), event.getMessage().getChannel(), GOOGLE_SEARCH.mainCommand(), e.getMessage());
            }
            return true;
        }

        if (SHORTEN_URL_COMMAND.mainCommand().equalsIgnoreCase(command)) {
            try {
                CommandLine cmd = SHORTEN_URL_COMMAND.parse(matchedText);

                if (SHORTEN_URL_COMMAND.showHelpIfPresent(event.getClient(), event.getMessage().getChannel(), cmd)) {
                    return true;
                }

                shortenURL(event, cmd.getOptionValue("t"));

            } catch (ParseException e) {
                logger.info("Parsing failed", e);
                EventUtils.sendIncorrectUsageMessage(event.getClient(), event.getMessage().getChannel(), GOOGLE_SEARCH.mainCommand(), e.getMessage());
            }
            return true;
        }

        return false;
    }

    private void shortenURL(@NotNull MessageReceivedEvent event, String url) throws RateLimitException, DiscordException, MissingPermissionsException {
        if (!UrlValidator.getInstance().isValid(url)) {
            EventUtils.sendIncorrectUsageMessage(event.getClient(), event.getMessage().getChannel(), "ShortUrl", "URL is not a valid http/https URL!");
            return;
        }
        try {
            Url shortUrl = GoogleClientServiceFactory.getInstance().getUrlShortenerService()
                                                     .url()
                                                     .insert(new Url().setLongUrl(url))
                                                     .execute();

            @NotNull StringBuilder builder = new StringBuilder(StringUtils.defaultIfEmpty(shortUrl.getId(), "URL was removed by google."));

            if (shortUrl.getStatus() != null && !"OK".equalsIgnoreCase(shortUrl.getStatus())) {
                builder.append("\n")
                       .append("Attention:")
                       .append("   ")
                       .append(shortUrl.getStatus());
            }

            new MessageBuilder(event.getClient())
                    .withChannel(event.getMessage().getChannel())
                    .withContent("Short URL: ")
                    .appendContent(builder.toString(), Styles.UNDERLINE)
                    .appendContent("\n")
                    .appendContent("Full URL: ")
                    .appendContent(url, Styles.UNDERLINE)
                    .send();

        } catch (IOException e) {
            logger.error("I/O Error during call to URLShortening service", e);
            new MessageBuilder(event.getClient())
                    .withChannel(event.getMessage().getChannel())
                    .withContent("Fail during google URL Shortening API Contact.")
                    .send();
        }
    }

    @NotNull
    @Override
    public List<Commands> getHandableCommands() {
        return Collections.singletonList(GOOGLE_SEARCH);
    }

}
