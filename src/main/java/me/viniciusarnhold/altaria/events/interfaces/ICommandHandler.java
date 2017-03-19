package me.viniciusarnhold.altaria.events.interfaces;

import me.viniciusarnhold.altaria.events.utils.Commands;
import org.jetbrains.annotations.Nullable;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

import java.util.List;

/**
 * Created by Vinicius.
 *
 * @since 1.0
 */
public interface ICommandHandler {

    boolean handle(MessageReceivedEvent event, String command, String matchedText) throws RateLimitException, DiscordException, MissingPermissionsException;

    @Nullable
    List<Commands> getHandableCommands();

}
