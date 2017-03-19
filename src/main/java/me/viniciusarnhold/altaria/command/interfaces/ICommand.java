package me.viniciusarnhold.altaria.command.interfaces;

import me.viniciusarnhold.altaria.command.CommandType;
import me.viniciusarnhold.altaria.command.UserPermissions;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.Set;

/**
 * Created by Vinicius.
 *
 * @since ${PROJECT_VERSION}
 */
public interface ICommand {

    @NotNull
    String command();

    @NotNull
    Set<String> aliases();

    @NotNull
    String description();

    @NotNull
    CommandType type();

    @NotNull
    EnumSet<UserPermissions> permissions();


}
