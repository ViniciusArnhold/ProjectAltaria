package me.viniciusarnhold.altaria.command.pool;

import org.jetbrains.annotations.NotNull;
import sx.blah.discord.handle.obj.IMessage;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Vinicius.
 *
 * @since ${PROJECT_VERSION}
 */
public class Pool {

    @NotNull
    private static final AtomicInteger POOL_COUNTER = new AtomicInteger(1);

    @NotNull
    private final Map<String, String> options;

    @NotNull
    private final long time;

    @NotNull
    private final IMessage message;

    @NotNull
    private final Integer poolID = POOL_COUNTER.getAndIncrement();

    @NotNull
    private final Type type;

    Pool(@NotNull Map<String, String> options, @NotNull IMessage message, @NotNull long time, @NotNull Type type) {
        this.options = options;
        this.message = message;
        this.time = time;
        this.type = type;
    }

    @NotNull
    public Map<String, String> options() {
        return options;
    }

    public long time() {
        return time;
    }

    @NotNull
    public IMessage message() {
        return message;
    }

    public Integer id() {
        return poolID;
    }

    public Type type() {
        return this.type;
    }

    public enum Type {

        MULTI,
        SINGLE

    }
}
