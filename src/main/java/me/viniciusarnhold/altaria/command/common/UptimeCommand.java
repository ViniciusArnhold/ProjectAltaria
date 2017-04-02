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

import java.util.Collections;
import java.util.EnumSet;

/**
 * Created by Vinicius.
 *
 * @since ${PROJECT_VERSION}
 */
public class UptimeCommand extends AbstractCommand {

    private static final Logger logger = LogManager.getLogger();

    public UptimeCommand() {
        this.command = "uptime";
        this.aliases = Collections.emptySet();
        this.commandType = CommandType.BOT;
        this.description = "Measures the time this bot has been onnline";
        this.permissions = EnumSet.noneOf(UserPermissions.class);
    }

    /**
     * Called when the event is sent.
     *
     * @param event The event object.
     */
    @Override
    public void handle(@NotNull MessageReceivedEvent event) {
        if (!isUptimeCommand(event)) {
            return;
        }
        logger.traceEntry("Received uptime command");

        try {
            MessageUtils.getDefaultRequestBuilder(event.getMessage())
                        .doAction(Actions.ofSuccess(() -> MessageUtils.getMessageBuilder(event.getMessage())
                                                                      .appendContent("This bot has been online for: ")
                                                                      .appendContent(TimeUtils.formatAsElapsed())
                                                                      .send()))
                        .andThen(Actions.ofSuccess(event.getMessage()::delete))
                        .execute();

        } catch (Exception e) {
            logger.error(e);
        }
    }

    private boolean isUptimeCommand(@NotNull MessageReceivedEvent event) {
        return !event.getMessage().getChannel().isPrivate() &&
                !event.getMessage().getAuthor().isBot() &&
                MessageUtils.isMyCommand(event.getMessage(), this);
    }
}
