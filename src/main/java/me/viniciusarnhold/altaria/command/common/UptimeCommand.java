package me.viniciusarnhold.altaria.command.common;

import me.viniciusarnhold.altaria.command.AbstractCommand;
import me.viniciusarnhold.altaria.command.CommandType;
import me.viniciusarnhold.altaria.command.MessageUtils;
import me.viniciusarnhold.altaria.command.UserPermissions;
import me.viniciusarnhold.altaria.utils.Actions;
import me.viniciusarnhold.altaria.utils.TimeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;

import java.lang.management.ManagementFactory;
import java.util.Collections;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

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
    public void handle(MessageReceivedEvent event) {
        if (!isUptimeCommand(event)) {
            return;
        }
        logger.traceEntry("Received uptime commandn");

        try {
            MessageUtils.getDefaultRequestBuilder(event.getMessage())
                    .doAction(Actions.ofSuccess(() -> MessageUtils.getMessageBuilder(event.getMessage())
                            .appendContent("This bot has been online for: ")
                            .appendContent(TimeUtils.formatToString(ManagementFactory.getRuntimeMXBean().getUptime(), TimeUnit.MILLISECONDS))
                            .send()))
                    .execute();

        } catch (Exception e) {
            logger.error(e);
        }
    }

    private boolean isUptimeCommand(MessageReceivedEvent event) {
        return !event.getMessage().getChannel().isPrivate() &&
                !event.getMessage().getAuthor().isBot() &&
                MessageUtils.isMyCommand(event.getMessage(), this);
    }
}
