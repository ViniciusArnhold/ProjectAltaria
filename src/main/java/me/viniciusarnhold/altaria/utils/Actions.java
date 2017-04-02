package me.viniciusarnhold.altaria.utils;

import me.viniciusarnhold.altaria.core.BotManager;
import org.jetbrains.annotations.NotNull;
import sx.blah.discord.util.*;



/**
 * Created by Vinicius.
 *
 * @since ${PROJECT_VERSION}
 */
public final class Actions {

    private Actions() {

    }


    @NotNull
    public static RequestBuilder.IRequestAction ofSuccess(@NotNull IWrappedVoidRequestAction action) throws RateLimitException, MissingPermissionsException, DiscordException {
        return () -> {
            action.execute();
            return true;
        };
    }

    @NotNull
    public static RequestBuilder.IRequestAction ofFailure(@NotNull IWrappedVoidRequestAction action) throws RateLimitException, MissingPermissionsException, DiscordException {
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

    public static <T> RequestBuffer.IRequest<T> wrap(@NotNull IWrappedRequestAction<T> supplier) {
        return () -> {
            try {
                return supplier.request();
            } catch (@NotNull DiscordException | MissingPermissionsException e) {
                BotManager.LOGGER.error(e);
                throw new RuntimeException(e);
            }
        };
    }

    public static RequestBuffer.IVoidRequest wrap(@NotNull IWrappedVoidRequestAction supplier) {
        return () -> {
            try {
                supplier.execute();
            } catch (@NotNull DiscordException | MissingPermissionsException e) {
                BotManager.LOGGER.error(e);
                throw new RuntimeException(e);
            }
        };
    }

    @FunctionalInterface
    public interface IWrappedVoidRequestAction {

        void execute() throws RateLimitException, MissingPermissionsException, DiscordException;

    }

    public interface IWrappedRequestAction<T> {


        @NotNull
        T request() throws RateLimitException, MissingPermissionsException, DiscordException;

    }
}
