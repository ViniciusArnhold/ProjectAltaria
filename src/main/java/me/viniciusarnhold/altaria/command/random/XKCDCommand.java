package me.viniciusarnhold.altaria.command.random;

import com.diffplug.common.base.Errors;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.viniciusarnhold.altaria.apis.HttpManager;
import me.viniciusarnhold.altaria.apis.objects.XKCDComic;
import me.viniciusarnhold.altaria.command.AbstractCommand;
import me.viniciusarnhold.altaria.command.CommandType;
import me.viniciusarnhold.altaria.command.MessageUtils;
import me.viniciusarnhold.altaria.command.UserPermissions;
import me.viniciusarnhold.altaria.events.utils.Commands;
import me.viniciusarnhold.altaria.utils.Actions;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;
import sx.blah.discord.util.RequestBuilder;

import java.io.IOException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Vinicius.
 *
 * @since ${PROJECT_VERSION}
 */
public class XKCDCommand extends AbstractCommand {

    private static final Logger logger = LogManager.getLogger();

    private static final String XKCD_NUMBERED_URL = "https://xkcd.com/%1s/info.0.json";

    private static final String XKCD_LATEST_URL = "https://xkcd.com/info.0.json";

    public XKCDCommand() {
        super();
        this.command = "xkcd";
        this.aliases = Collections.emptySet();
        this.commandType = CommandType.GENERAL;
        this.description = "Posts links of XKCD.com, use with xkcd <num>, xkcd random, xkcd latest";
        this.permissions = EnumSet.noneOf(UserPermissions.class);
    }

    /**
     * Called when the event is sent.
     *
     * @param event The event object.
     */
    @Override
    public void handle(@NotNull MessageReceivedEvent event) {
        if (!isXKCDCommand(event)) {
            return;
        }
        try {
            logger.traceEntry("Received XKCD Command {}", event.getMessage().getContent());
            @NotNull List<String> args = Commands.splitByWhitespace(event.getMessage().getContent());
            RequestBuilder requestBuilder =
                    MessageUtils.getDefaultRequestBuilder(event.getMessage())
                            .doAction(Actions.ofSuccess());

            if (args.size() < 2 || "latest".equalsIgnoreCase(args.get(1))) {

                requestBuilder.andThen(getComic(XKCD_LATEST_URL, event));

            } else if ("random".equalsIgnoreCase(args.get(1))) {

                @Nullable XKCDComic latest = getLatestComic();
                int num;
                if (latest != null) {
                    num = ThreadLocalRandom.current().nextInt(1, latest.getComicNumber() + 1);
                } else {
                    num = ThreadLocalRandom.current().nextInt(1, 1800);
                }

                requestBuilder.andThen(getComic(String.format(XKCD_NUMBERED_URL, num), event));

            } else if (StringUtils.isNumericSpace(args.get(1))) {

                requestBuilder.andThen(getComic(String.format(XKCD_NUMBERED_URL, Integer.parseUnsignedInt(args.get(1))), event));

            } else {
                requestBuilder.andThen(Actions.ofSuccess(() -> MessageUtils.getSimpleMentionMessage(event.getMessage())
                        .appendContent("Unknown args { " + args.get(1) + " }")
                        .appendContent(System.lineSeparator())
                        .appendContent(this.description)
                        .send()));
            }
            requestBuilder
                    .andThen(Actions.ofSuccess(event.getMessage()::delete))
                    .execute();

        } catch (Exception e) {
            logger.error("Failed to handle command", e);
        }
    }

    private XKCDComic getLatestComic() {
        Request request = new Request.Builder()
                .url("http://xkcd.com/info.0.json")
                .build();

        try (Response response = HttpManager.getInstance().getDefaultClient().newCall(request).execute()) {

            if (response.isSuccessful()) {

                @NotNull final ObjectMapper mapper = new ObjectMapper();

                return mapper.readValue(response.body().string(), XKCDComic.class);
            }
        } catch (IOException e) {
            logger.error(e);
        }
        return null;
    }

    private RequestBuilder.IRequestAction getComic(@NotNull String url, @NotNull MessageReceivedEvent event) throws RateLimitException, DiscordException, MissingPermissionsException {

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = HttpManager.getInstance().getDefaultClient().newCall(request).execute()) {

            if (response.isSuccessful()) {

                @NotNull final ObjectMapper mapper = new ObjectMapper();

                final XKCDComic comic = mapper.readValue(response.body().string(), XKCDComic.class);

                return Actions.ofSuccess(() ->
                        MessageUtils.getMessageBuilder(event.getMessage())
                                .withEmbed(
                                        MessageUtils.getEmbedBuilder(event.getMessage().getAuthor())
                                                .ignoreNullEmptyFields()
                                                .withTitle(comic.getComicNumber() + " : " + comic.getTitle())
                                                .withDescription("Posted on: " + (String.format("%1s/%s/%1s", comic.getDay(), comic.getMonth(), comic.getYear())))
                                                .appendField("Alt Text", comic.getAltText(), false)
                                                .withImage(comic.getImageLink())
                                                .build())
                                .send());


            } else {
                if (response.code() == 404) {
                    return Actions.ofSuccess(() ->
                            MessageUtils.getMessageBuilder(event.getMessage())
                                    .appendContent("Commic not found")
                                    .send());
                } else {
                    return Actions.ofSuccess(() ->
                            Errors.log().wrap(() ->
                                    MessageUtils.getMessageBuilder(event.getMessage())
                                            .appendContent("Failed to contact XKCD.com, ")
                                            .appendContent("Http Error: ")
                                            .appendContent(Integer.toString(response.code()))
                                            .appendContent(System.lineSeparator())
                                            .appendContent(response.body().string())
                                            .send()));
                }
            }
        } catch (IOException e) {
            logger.error(e);
            return Actions.ofSuccess(() -> MessageUtils.getMessageBuilder(event.getMessage()).appendContent(e.getMessage()).send());
        }
    }

    private boolean isXKCDCommand(@NotNull MessageReceivedEvent event) {
        return !event.getMessage().getChannel().isPrivate() &&
                !event.getMessage().getAuthor().isBot() &&
                MessageUtils.isMyCommand(event.getMessage(), this);
    }
}
