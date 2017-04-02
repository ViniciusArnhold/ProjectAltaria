package me.viniciusarnhold.altaria.command.common;

import me.viniciusarnhold.altaria.command.AbstractCommand;
import me.viniciusarnhold.altaria.command.CommandType;
import me.viniciusarnhold.altaria.command.MessageUtils;
import me.viniciusarnhold.altaria.command.UserPermissions;
import me.viniciusarnhold.altaria.utils.Actions;
import me.viniciusarnhold.altaria.utils.TimeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

import java.util.Collections;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

/**
 * Created by Vinicius.
 *
 * @since ${PROJECT_VERSION}
 */
public class PingCommand extends AbstractCommand {

    private static final Logger logger = LogManager.getLogger();

    public PingCommand() {
        this.command = "ping";
        this.aliases = Collections.emptySet();
        this.commandType = CommandType.BOT;
        this.description = "Measures the time a command take to reach the bot";
        this.permissions = EnumSet.noneOf(UserPermissions.class);
    }

    /**
     * Called when the event is sent.
     *
     * @param event The event object.
     */
    @Override
    public void handle(@NotNull final MessageReceivedEvent event) {
        if (!isPingCommand(event)) {
            return;
        }
        logger.traceEntry("Received ping command.");

        try {
            MessageUtils.getDefaultRequestBuilder(event.getMessage())
                        .doAction(Actions.ofSuccess(() -> MessageUtils.getMessageBuilder(event.getMessage())
                            .appendContent("Pong!")
                            .appendContent(System.lineSeparator())
                            .appendContent("Last reponse time in: ")
                            .appendContent(TimeUtils.formatToString(event.getMessage().getShard().getResponseTime(), TimeUnit.MILLISECONDS))
                            .send()))
                        .andThen(Actions.ofSuccess(event.getMessage()::delete))
                        .execute();

        } catch (@NotNull RateLimitException | MissingPermissionsException | DiscordException e) {
            logger.error(e);
        }
    }

    private boolean isPingCommand(@NotNull MessageReceivedEvent event) {
        return !event.getMessage().getChannel().isPrivate() &&
                !event.getMessage().getAuthor().isBot() &&
                MessageUtils.isMyCommand(event.getMessage(), this);
    }
}
