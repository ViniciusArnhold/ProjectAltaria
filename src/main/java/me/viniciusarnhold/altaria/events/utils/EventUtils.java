package me.viniciusarnhold.altaria.events.utils;

import me.viniciusarnhold.altaria.events.EventManager;
import org.apache.http.HttpStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jsoup.HttpStatusException;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

import static org.apache.commons.lang3.StringUtils.SPACE;

/**
 * Created by Vinicius.
 *
 * @since ${PROJECT_VERSION}
 */
public class EventUtils {

    private EventUtils() {
        throw new Error();
    }

    public static void sendConnectionErrorMessage(IDiscordClient client, IChannel channel, String command, @Nullable String message, @NotNull HttpStatusException httpe) throws RateLimitException, DiscordException, MissingPermissionsException {
        @NotNull String problem = message != null ? message + "\n" : "";
        if (httpe.getStatusCode() == HttpStatus.SC_SERVICE_UNAVAILABLE) {
            problem += "Service unavailable, please try again latter.";
        } else if (httpe.getStatusCode() == HttpStatus.SC_FORBIDDEN) {
            problem += "Acess dennied.";
        } else if (httpe.getStatusCode() == HttpStatus.SC_NOT_FOUND) {
            problem += "Not Found";
        } else {
            problem += httpe.getStatusCode() + SPACE + httpe.getMessage();
        }

        new MessageBuilder(client)
                .appendContent("Error during HTTP Connection ", MessageBuilder.Styles.BOLD)
                .appendContent("\n")
                .appendContent(EventManager.MAIN_COMMAND_NAME, MessageBuilder.Styles.BOLD)
                .appendContent(SPACE)
                .appendContent(command, MessageBuilder.Styles.BOLD)
                .appendContent("\n")
                .appendContent(problem, MessageBuilder.Styles.BOLD)
                .withChannel(channel)
                .send();
    }

    public static void sendIncorrectUsageMessage(IDiscordClient client, IChannel channel, String command, String reason) throws RateLimitException, DiscordException, MissingPermissionsException {
        new MessageBuilder(client)
                .appendContent("Incorrect usage of command: ", MessageBuilder.Styles.BOLD)
                .appendContent(command, MessageBuilder.Styles.BOLD)
                .appendContent("\n")
                .appendContent(reason, MessageBuilder.Styles.BOLD)
                .withChannel(channel)
                .send();
    }

}
