package me.viniciusarnhold.altaria.utils;

import org.jetbrains.annotations.NotNull;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;
import sx.blah.discord.util.RequestBuilder;

/**
 * Created by Vinicius.
 *
 * @since ${PROJECT_VERSION}
 */
public final class Actions {

    private Actions() {

    }


    @NotNull
    public static RequestBuilder.IRequestAction ofSuccess(@NotNull IWrappedRequestAction action) throws RateLimitException, MissingPermissionsException, DiscordException {
        return () -> {
            action.execute();
            return true;
        };
    }

    @NotNull
    public static RequestBuilder.IRequestAction ofFailure(@NotNull IWrappedRequestAction action) throws RateLimitException, MissingPermissionsException, DiscordException {
        return () -> {
            action.execute();
            return false;
        };
    }

    public static RequestBuilder.IRequestAction ofSuccess() throws RateLimitException, MissingPermissionsException, DiscordException {
        return () -> true;
    }

    public static RequestBuilder.IRequestAction ofFailure() throws RateLimitException, MissingPermissionsException, DiscordException {
        return () -> false;
    }

    @FunctionalInterface
    public interface IWrappedRequestAction {

        void execute() throws RateLimitException, MissingPermissionsException, DiscordException;

    }
}
