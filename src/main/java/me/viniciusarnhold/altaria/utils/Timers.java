package me.viniciusarnhold.altaria.utils;

import me.viniciusarnhold.altaria.command.MessageUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Created by Vinicius.
 *
 * @since ${PROJECT_VERSION}
 */
public class Timers {

    private static Timer cacheCleanUpTimer;

    @Contract(" -> fail")
    private Timers() {
        throw new Error("No init");
    }

    @NotNull
    public static final CacheCleanUpService cacheCleanUpTimer() {
        return CacheCleanUpService.getInstance();
    }

    @NotNull
    public static final MessageDeletionService messageDeletionService() {
        return MessageDeletionService.getInstance();
    }

    public static final class MessageDeletionService {

        public static final Logger logger = LogManager.getLogger();

        private static final MessageDeletionService instance = new MessageDeletionService();

        @NotNull
        private final Timer timer;

        private MessageDeletionService() {
            this.timer = new Timer("Message Deletion Service Timer", true);
        }

        @NotNull
        public static MessageDeletionService getInstance() {
            return instance;
        }

        public void schedule(@NotNull IMessage message) {
            schedule(message, 60000L);
        }

        public void schedule(@NotNull IMessage message, long time, TimeUnit originUnit) {
            schedule(message, TimeUnit.MILLISECONDS.convert(time, originUnit));
        }

        public void schedule(@NotNull IMessage message, long nanos) {
            timer.schedule(newMessageDeletionTask(message), nanos);
        }

        @NotNull
        private final TimerTask newMessageDeletionTask(@NotNull IMessage message) {
            return new TimerTask() {
                @Override
                public void run() {
                    try {
                        message.delete();
                    } catch (@NotNull MissingPermissionsException | RateLimitException | DiscordException e) {
                        MessageUtils.handleDiscord4JException(logger, e);
                    }
                }
            };
        }
    }

    public static final class CacheCleanUpService {

        private static final CacheCleanUpService ourInstance = new CacheCleanUpService();

        @NotNull
        private final Timer cacheCleanUpTimer;

        private CacheCleanUpService() {
            cacheCleanUpTimer = new Timer("Cache cleanup Timer Service.", true);
        }

        @NotNull
        private static final CacheCleanUpService getInstance() {
            return ourInstance;
        }

        public <K, V> void schedule(@NotNull Map<K, V> mapToClean, K keyToClean, V expectedValue) {
            schedule(mapToClean, keyToClean, expectedValue, 1, TimeUnit.HOURS);
        }

        public <K, V> void schedule(@NotNull Map<K, V> mapToClean, K keyToClean, V expectedValue, long time, TimeUnit unit) {
            cacheCleanUpTimer.schedule(
                    createNewCleanUpTask(mapToClean, keyToClean, expectedValue),
                    TimeUnit.NANOSECONDS.convert(time, unit)
                                      );
        }

        @NotNull
        private final <K, V> TimerTask createNewCleanUpTask(@NotNull final Map<K, V> mapToClean, final K keyToClean, V expectedValue) {
            return new TimerTask() {
                @Override
                public void run() {
                    mapToClean.remove(keyToClean, expectedValue);
                }
            };
        }
    }


}
