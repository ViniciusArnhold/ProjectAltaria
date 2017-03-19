package me.viniciusarnhold.altaria.events;

import com.google.common.base.Stopwatch;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Vinicius.
 *
 * @since ${PROJECT_VERSION}
 */
public final class StatsCounter {

    private static final AtomicLong commandsHandled = new AtomicLong();

    private static final ArrayList<Long> commandHandleTime = new ArrayList<>();

    public static final void addCommandHandled(long num) {
        commandsHandled.addAndGet(num);
    }

    public static final long commandsHandled() {
        return commandsHandled.get();
    }

    public static final void addCommandHandleTime(Stopwatch watch) {
        commandHandleTime.add(Objects.requireNonNull(watch).elapsed(TimeUnit.NANOSECONDS));
    }

    public static final void addCommandHanndleTime(long nanos) {
        commandHandleTime.add(nanos);
    }

    public static final long meanCommandHandleTime() {
        synchronized (commandHandleTime) {
            return (long) commandHandleTime.stream().mapToLong(l -> l).average().orElse(0L);
        }
    }

    public static final long maxCommandHandleTime() {
        synchronized (commandHandleTime) {
            return commandHandleTime.stream().mapToLong(l -> l).max().orElse(0L);
        }
    }

    public static final long minCommandHandleTime() {
        synchronized (commandHandleTime) {
            return commandHandleTime.stream().mapToLong(l -> l).min().orElse(0L);
        }
    }

}
