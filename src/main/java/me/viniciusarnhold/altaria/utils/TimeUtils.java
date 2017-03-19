package me.viniciusarnhold.altaria.utils;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.*;

/**
 * Created by Vinicius.
 *
 * @since ${PROJECT_VERSION}
 */
public class TimeUtils {

    private TimeUtils() {
        throw new Error("No init");
    }

    @NotNull
    public static String formatToString(long nanos) {
        TimeUnit target = chooseTimeUnit(nanos);
        return target.convert(nanos, NANOSECONDS) + " " + abbreviate(target);
    }


    @NotNull
    public static String formatToString(long timeValue, @NotNull TimeUnit timeUnit) {
        return formatToString(timeUnit.toNanos(timeValue));
    }

    //Who knows why google made this classes Private in StopWatch
    @NotNull
    private static TimeUnit chooseTimeUnit(long nanos) {
        if (DAYS.convert(nanos, NANOSECONDS) > 0) {
            return DAYS;
        }
        if (HOURS.convert(nanos, NANOSECONDS) > 0) {
            return HOURS;
        }
        if (MINUTES.convert(nanos, NANOSECONDS) > 0) {
            return MINUTES;
        }
        if (SECONDS.convert(nanos, NANOSECONDS) > 0) {
            return SECONDS;
        }
        if (MILLISECONDS.convert(nanos, NANOSECONDS) > 0) {
            return MILLISECONDS;
        }
        if (MICROSECONDS.convert(nanos, NANOSECONDS) > 0) {
            return MICROSECONDS;
        }
        return NANOSECONDS;
    }

    public static String abbreviate(@NotNull TimeUnit unit) {
        switch (unit) {
            case NANOSECONDS:
                return "ns";
            case MICROSECONDS:
                return "\u03bcs"; // μs
            case MILLISECONDS:
                return "ms";
            case SECONDS:
                return "s";
            case MINUTES:
                return "min";
            case HOURS:
                return "h";
            case DAYS:
                return "d";
            default:
                throw new IllegalArgumentException("unit must not be null");
        }

    }
}
