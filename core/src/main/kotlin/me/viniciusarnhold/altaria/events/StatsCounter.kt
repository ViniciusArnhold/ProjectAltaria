package me.viniciusarnhold.altaria.events

import com.google.common.base.Stopwatch
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

/**
 * Created by Vinicius.

 * @since ${PROJECT_VERSION}
 */
object StatsCounter {

    private val commandsHandled = AtomicLong()

    private val commandHandleTime = ArrayList<Long>()

    fun addCommandHandled(num: Long) {
        commandsHandled.addAndGet(num)
    }

    fun commandsHandled(): Long {
        return commandsHandled.get()
    }

    fun addCommandHandleTime(watch: Stopwatch) {
        commandHandleTime.add(Objects.requireNonNull(watch).elapsed(TimeUnit.NANOSECONDS))
    }

    fun addCommandHanndleTime(nanos: Long) {
        commandHandleTime.add(nanos)
    }

    fun meanCommandHandleTime(): Long {
        synchronized(commandHandleTime) {
            return commandHandleTime.stream().mapToLong { l -> l }.average().orElse(0.0).toLong()
        }
    }

    fun maxCommandHandleTime(): Long {
        synchronized(commandHandleTime) {
            return commandHandleTime.stream().mapToLong { l -> l }.max().orElse(0L)
        }
    }

    fun minCommandHandleTime(): Long {
        synchronized(commandHandleTime) {
            return commandHandleTime.stream().mapToLong { l -> l }.min().orElse(0L)
        }
    }

}
