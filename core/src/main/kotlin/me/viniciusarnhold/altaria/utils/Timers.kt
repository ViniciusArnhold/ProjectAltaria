package me.viniciusarnhold.altaria.utils

import commands.MessageUtils
import org.apache.logging.log4j.LogManager
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.util.DiscordException
import sx.blah.discord.util.MissingPermissionsException
import sx.blah.discord.util.RateLimitException
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by Vinicius.

 * @since ${PROJECT_VERSION}
 */
object Timers {

    object MessageDeletionService {

        private val timer: Timer = Timer("Message Deletion Service Timer", true)

        fun schedule(message: IMessage, time: Long, originUnit: TimeUnit) {
            schedule(message, TimeUnit.MILLISECONDS.convert(time, originUnit))
        }

        @JvmOverloads fun schedule(message: IMessage, nanos: Long = 60000L) {
            timer.schedule(newMessageDeletionTask(message), nanos)
        }

        fun IMessage.deleteIn(nanos: Long = 60000L) {
            timer.schedule(newMessageDeletionTask(this), nanos)
        }

        private fun newMessageDeletionTask(message: IMessage): TimerTask {
            return object : TimerTask() {
                override fun run() {
                    try {
                        message.delete()
                    } catch (e: MissingPermissionsException) {
                        MessageUtils.handleDiscord4JException(logger, e)
                    } catch (e: RateLimitException) {
                        MessageUtils.handleDiscord4JException(logger, e)
                    } catch (e: DiscordException) {
                        MessageUtils.handleDiscord4JException(logger, e)
                    }

                }
            }
        }

        val logger = LogManager.getLogger()!!
    }

    object CacheCleanUpService {

        private val cacheCleanUpTimer: Timer = Timer("Cache cleanup Timer Service.", true)

        fun <K, V> schedule(mapToClean: MutableMap<K, V>, keyToClean: K, expectedValue: V) {
            schedule(mapToClean, keyToClean, expectedValue, 1, TimeUnit.HOURS)
        }

        fun <K, V> schedule(mapToClean: MutableMap<K, V>, keyToClean: K, expectedValue: V, time: Long, unit: TimeUnit) {
            cacheCleanUpTimer.schedule(
                    createNewCleanUpTask(mapToClean, keyToClean, expectedValue),
                    TimeUnit.NANOSECONDS.convert(time, unit)
            )
        }

        private fun <K, V> createNewCleanUpTask(mapToClean: MutableMap<K, V>, keyToClean: K, expectedValue: V): TimerTask {
            return object : TimerTask() {
                override fun run() {
                    mapToClean.remove(keyToClean, expectedValue)
                }
            }
        }
    }
}
