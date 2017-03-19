package me.viniciusarnhold.altaria.command;

import me.viniciusarnhold.altaria.command.interfaces.ICommand;
import org.jetbrains.annotations.NotNull;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * Created by Vinicius.
 *
 * @since ${PROJECT_VERSION}
 */
public abstract class AbstractCommand implements ICommand, IListener<MessageReceivedEvent> {

    protected String command;

    protected Set<String> aliases;

    protected String description;

    protected CommandType commandType;

    protected EnumSet<UserPermissions> permissions;

    public AbstractCommand() {
        command = "";
        aliases = Collections.emptySet();
        description = "";
        commandType = CommandType.GENERAL;
        permissions = EnumSet.noneOf(UserPermissions.class);
    }


    @NotNull
    @Override
    public String command() {
        return command;
    }

    @NotNull
    @Override
    public Set<String> aliases() {
        return aliases;
    }

    @NotNull
    @Override
    public String description() {
        return description;
    }

    @NotNull
    @Override
    public CommandType type() {
        return commandType;
    }

    @NotNull
    @Override
    public EnumSet<UserPermissions> permissions() {
        return permissions;
    }
}
