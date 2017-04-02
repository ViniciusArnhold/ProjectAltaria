package me.viniciusarnhold.altaria.command;

import com.diffplug.common.base.Errors;
import me.viniciusarnhold.altaria.command.interfaces.ICommand;
import me.viniciusarnhold.altaria.core.BotManager;
import me.viniciusarnhold.altaria.utils.Logs;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IDiscordObject;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.*;

import java.time.LocalDateTime;
import java.util.function.Supplier;

import static me.viniciusarnhold.altaria.utils.Timers.messageDeletionService;

/**
 * Created by Vinicius.
 *
 * @since ${PROJECT_VERSION}
 */
public class MessageUtils {

    private static final String ICON_URL = "https://vignette2.wikia.nocookie.net/pokemon--shuffle/images/e/e2/334.png/revision/latest?cb=20150912015748";

    private MessageUtils() {

    }


    public static boolean isMyCommand(@NotNull IMessage message, @NotNull ICommand thiz) {
        return isMyCommand(message.getContent(), thiz);
    }

    public static boolean isMyCommand(String message, @NotNull ICommand thiz) {
        message = message.trim();
        @NotNull final String prefix = Prefixes.getInstance().current();
        if (!message.startsWith(prefix)) {
            return false;
        }
        if (StringUtils.startsWithIgnoreCase(message, prefix + thiz.command())) {
            return true;
        }

        for (String alias : thiz.aliases()) {
            if (StringUtils.startsWithIgnoreCase(message, prefix + alias)) {
                return true;
            }
        }
        return false;
    }

    public static EmbedBuilder getEmbedBuilder() {
        return new EmbedBuilder()
                .withAuthorName(BotManager.BOT_NAME)
                .withAuthorUrl(BotManager.REPO_URL)
                .withColor(36, 177, 222)
                .withAuthorIcon(ICON_URL)
                .withTimestamp(LocalDateTime.now());
    }

    public static EmbedBuilder getEmbedBuilder(@NotNull IUser user) {
        return new EmbedBuilder()
                .withAuthorName(BotManager.BOT_NAME)
                .withAuthorUrl(BotManager.REPO_URL)
                .withColor(36, 177, 222)
                .withAuthorIcon(ICON_URL)
                .withFooterIcon(user.getAvatarURL())
                .withFooterText("Requested by @" + user.getName())
                .withTimestamp(LocalDateTime.now());
    }

    public static RequestBuilder getDefaultRequestBuilder(@NotNull IDiscordObject obj) {
        return new RequestBuilder(obj.getClient())
                .shouldBufferRequests(true)
                .onTimeout(() -> Logs.forMessage("Timeout while executing requests on default requestBuilder"))
                .setAsync(true)
                .shouldFailOnException(false);
    }

    public static MessageBuilder getMessageBuilder(@NotNull IMessage message) {
        return getMessageBuilder(message.getChannel());
    }

    public static MessageBuilder getMessageBuilder(@NotNull IChannel channel) {
        return new MessageBuilder(channel.getClient())
                .withChannel(channel);
    }

    public static MessageBuilder getSimpleMentionMessage(@NotNull IMessage message) {
        return getMessageBuilder(message)
                .withContent(message.getAuthor().mention())
                .appendContent(" ");
    }

    @NotNull
    public static <T> T retry(@NotNull Supplier<T> supplier) {
        @org.jetbrains.annotations.Nullable T value = null;
        do {
            try {
                value = supplier.get();
            } catch (Exception rle) {
                if (!(rle instanceof RateLimitException)) {
                    BotManager.LOGGER.error(rle);
                    throw new RuntimeException(rle);
                }
                try {
                    Thread.sleep(((RateLimitException) rle).getRetryDelay());
                } catch (InterruptedException e) {
                    BotManager.LOGGER.error(e);
                }
            }
        } while (value == null);
        return value;
    }

    public static void handleDiscord4JException(@NotNull Logger logger, Exception e) {
        logger.error(e);
    }

    public static <T> T supress(@NotNull Supplier<T> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            BotManager.LOGGER.error(e);
            throw Errors.asRuntime(e);
        }
    }

    public static void handleDiscord4JException(@NotNull Logger logger, @NotNull Exception e, @NotNull ICommand commandHandler, @NotNull IMessage message) {
        try {
            logger.error("{} failed to handle command, a {} was captured",
                    () -> commandHandler.getClass().getSimpleName(),
                    () -> e.getClass().getSimpleName());
            logger.error(e);

            if (e instanceof MissingPermissionsException) {
                messageDeletionService().schedule(getMessageBuilder(message)
                        .appendContent(message.getAuthor().mention())
                        .appendContent(" I dont have the necessary permissions to execute that action,")
                        .appendContent(" please give me the following permissions and try again")
                        .appendContent(System.lineSeparator())
                        .appendContent(((MissingPermissionsException) e).getMissingPermissions().toString())
                        .send());
            }

        } catch (@NotNull RateLimitException | DiscordException | MissingPermissionsException e1) {
            logger.error(e1);
        }
    }
}
