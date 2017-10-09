package me.viniciusarnhold.altaria.utils

import java.lang.management.ManagementFactory
import java.time.Duration
import java.util.concurrent.TimeUnit

import java.util.concurrent.TimeUnit.*

/**
 * Created by Vinicius.

 * @since ${PROJECT_VERSION}
 */
class TimeUtils private constructor() {

    init {
        throw Error("No init")
    }

    companion object {

        private val TIME_FORMAT = "%2s Days %2s Hours %2s Minutes %2s Seconds %4s Milliseconds"

        fun formatAsElapsed(): String {
            var elapsed = Duration.ofMillis(ManagementFactory.getRuntimeMXBean().uptime)
            val days = elapsed.toDays()
            elapsed = elapsed.minusDays(days)
            val hours = elapsed.toHours()
            elapsed = elapsed.minusHours(hours)
            val minutes = elapsed.toMinutes()
            elapsed = elapsed.minusMinutes(minutes)
            val seconds = elapsed.seconds
            elapsed = elapsed.minusSeconds(seconds)
            val millis = elapsed.toMillis()

            return String.format(TIME_FORMAT,
                    days,
                    hours,
                    minutes,
                    seconds,
                    millis)
        }

        fun formatToString(nanos: Long): String {
            val target = chooseTimeUnit(nanos)
            return target.convert(nanos, NANOSECONDS).toString() + " " + abbreviate(target)
        }


        fun formatToString(timeValue: Long, timeUnit: TimeUnit): String {
            return formatToString(timeUnit.toNanos(timeValue))
        }

        //Who knows why google made this classes Private in StopWatch
        private fun chooseTimeUnit(nanos: Long): TimeUnit {
            if (DAYS.convert(nanos, NANOSECONDS) > 0) {
                return DAYS
            }
            if (HOURS.convert(nanos, NANOSECONDS) > 0) {
                return HOURS
            }
            if (MINUTES.convert(nanos, NANOSECONDS) > 0) {
                return MINUTES
            }
            if (SECONDS.convert(nanos, NANOSECONDS) > 0) {
                return SECONDS
            }
            if (MILLISECONDS.convert(nanos, NANOSECONDS) > 0) {
                return MILLISECONDS
            }
            if (MICROSECONDS.convert(nanos, NANOSECONDS) > 0) {
                return MICROSECONDS
            }
            return NANOSECONDS
        }

        fun abbreviate(unit: TimeUnit): String {
            when (unit) {
                NANOSECONDS -> return "ns"
                MICROSECONDS -> return "\u03bcs" // μs
                MILLISECONDS -> return "ms"
                SECONDS -> return "s"
                MINUTES -> return "min"
                HOURS -> return "h"
                DAYS -> return "d"
                else -> throw IllegalArgumentException("unit unknown")
            }

        }
    }
}
