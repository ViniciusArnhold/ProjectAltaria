package me.viniciusarnhold.altaria.events.thirdparty;

import me.viniciusarnhold.altaria.events.EventManager;
import me.viniciusarnhold.altaria.events.IDiscordEvent;
import me.viniciusarnhold.altaria.events.utils.EventUtils;
import me.viniciusarnhold.altaria.events.utils.RegexEvents;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Vinicius.
 *
 * @since 131
 */
public class GoogleSearchEvent implements IDiscordEvent, IListener<MessageReceivedEvent> {

    private static final Logger logger = LogManager.getLogger();
    private static final String BOT_USER_AGENT = "ProjectAltaria 1.0 (+http://viniciusarnhold.me)";
    private static final String GOOGLE_SEARCH_LINK = "http://www.google.com/search?q=";
    private static GoogleSearchEvent ourInstance = new GoogleSearchEvent();

    static {
        EventManager.registerCommand("Pesquisa::Google<::Limitar=x<=5> \"args\"", "Search Google, optionally limiting the number of results by a max of 5", RegexEvents.SIMPLE_ARGS);
        EventManager.registerCommand("Pesquisa::Youtube<::Limitar=x<=5> \"args\"", "Search Youtube, optionally limiting the number of results by a max of 5", RegexEvents.SIMPLE_ARGS);
    }

    public GoogleSearchEvent() {
    }

    public static GoogleSearchEvent getInstance() {
        return ourInstance;
    }


    /**
     * Called when the event is sent.
     *
     * @param event The event object.
     */
    @Override
    public void handle(MessageReceivedEvent event) {
        IMessage message = event.getMessage();
        Matcher matcher = RegexEvents.SIMPLE_ARGS.getRegex().matcher(message.getContent().trim());

        if (matcher.find()) {
            EventUtils.logMessageEventReceived(event, this.getClass());
            try {
                switch (matcher.group(1).toLowerCase().split("::")[0]) {
                    case "pesquisa":
                        handleSearch(event, matcher);
                        break;
                    default:
                        //NoMessage
                        break;
                }
            } catch (Exception e) {
                logger.error("Failed during " + this.getClass().getSimpleName() + " event handling.", e);
            }
        }
    }

    private void handleSearch(MessageReceivedEvent event, Matcher matcher) throws DiscordException, MissingPermissionsException, RateLimitException {
        String[] command = matcher.group(1).toLowerCase().split("::");
        if (command.length >= 2) {
            switch (command[1]) {
                case "google":
                    int limite = Integer.MAX_VALUE;
                    if (command.length == 3) {
                        if (Pattern.matches("limitar=\\d{1,5}", command[2])) {
                            limite = Integer.valueOf(command[2].split("=")[1]);
                        }
                    }
                    googleSearch(event, matcher, limite);
                    break;
                default:
                    break;
            }
        }
    }

    private void googleSearch(MessageReceivedEvent event, Matcher matcher, int limit) throws DiscordException, MissingPermissionsException, RateLimitException {
        String search = matcher.group(2);

        Elements links;
        try {
            links = Jsoup.connect(GOOGLE_SEARCH_LINK + URLEncoder.encode(search, StandardCharsets.UTF_8.name()))
                    .userAgent(BOT_USER_AGENT)
                    .get()
                    .select(".g>.r>a");
        } catch (IOException e) {
            logger.error("Failed to connect to google during google search", e);
            return;
        }

        MessageBuilder builder = new MessageBuilder(EventManager.getDiscordClient())
                .withChannel(event.getMessage().getChannel());
        int count = 0;
        for (Element link : links) {
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
            builder.appendContent(url, MessageBuilder.Styles.ITALICS);
            builder.appendContent("\n");
            count++;
        }
        builder.send();
    }

    @Override
    public void terminate() {
    }
}
