package me.viniciusarnhold.altaria.command.common;

import com.google.common.collect.ImmutableSet;
import me.viniciusarnhold.altaria.command.CommandType;
import me.viniciusarnhold.altaria.command.MessageUtils;
import me.viniciusarnhold.altaria.command.UserPermissions;
import me.viniciusarnhold.altaria.command.interfaces.ICommand;
import me.viniciusarnhold.altaria.core.BotManager;
import me.viniciusarnhold.altaria.events.utils.Commands;
import me.viniciusarnhold.altaria.utils.Actions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.RateLimitException;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Vinicius.
 *
 * @since ${PROJECT_VERSION}
 */
public class InviteCommand implements IListener<MessageReceivedEvent>, ICommand {

    private static final Logger logger = LogManager.getLogger();

    private static final String command = "Invite";

    private static final Set<String> alias = ImmutableSet.of("Inv", "Invitation");

    private static final String desc = "Returns the invite link for this bot.";

    private static final CommandType type = CommandType.BOT;

    private static final EnumSet<UserPermissions> permissions = EnumSet.noneOf(UserPermissions.class);

    /**
     * Called when the event is sent.
     *
     * @param event The event object.
     */
    @Override
    public void handle(MessageReceivedEvent event) {
        if (!isInviteCommand(event)) {
            return;
        }
        try {

            logger.traceEntry("Received Invite command with {}", () -> event.getMessage().getContent());

            List<String> args = Commands.splitByWhitespace(event.getMessage().getContent().trim());

            boolean isLocal = false;
            if (args.size() > 1 && args.get(1).equals("here")) {
                isLocal = true;
            }

            final IChannel channelToSend;
            if (!isLocal) {
                IChannel channel;
                try {
                    channel = event.getMessage().getAuthor().getOrCreatePMChannel();
                } catch (DiscordException | RateLimitException de) {
                    logger.error(de);
                    channel = event.getMessage().getChannel();
                }
                channelToSend = channel;
            } else {
                channelToSend = event.getMessage().getChannel();
            }

            MessageBuilder builder = MessageUtils.getMessageBuilder(event.getMessage())
                    .withChannel(channelToSend)
                    .appendContent("Here's my invite link!")
                    .appendContent(System.lineSeparator())
                    .appendContent(BotManager.getInstance().inviteUrl());

            MessageUtils.getDefaultRequestBuilder(event.getMessage())
                    .doAction(Actions.ofSuccess(builder::send))
                    .andThen(Actions.ofSuccess(event.getMessage()::delete))
                    .execute();

        } catch (Exception e) {
            logger.error("Failed to handle Invite command", e);
        }
    }

    private boolean isInviteCommand(MessageReceivedEvent event) {
        return !event.getMessage().getChannel().isPrivate() &&
                !event.getMessage().getAuthor().isBot() &&
                MessageUtils.isMyCommand(event.getMessage(), this);
    }

    @NotNull
    @Override
    public String command() {
        return command;
    }

    @NotNull
    @Override
    public Set<String> aliases() {
        return alias;
    }

    @NotNull
    @Override
    public String description() {
        return desc;
    }

    @NotNull
    @Override
    public CommandType type() {
        return type;
    }

    @NotNull
    @Override
    public EnumSet<UserPermissions> permissions() {
        return permissions;
    }
}
