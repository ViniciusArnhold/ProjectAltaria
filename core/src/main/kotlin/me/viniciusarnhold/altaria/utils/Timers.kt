package me.viniciusarnhold.altaria.utils

import org.apache.logging.log4j.LogManager
import sx.blah.discord.handle.obj.IMessage
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by Vinicius.

 * @since 1.0
 */
object Timers {

    object MessageDeletionService {

        private val timer: Timer = Timer("Message Deletion Service Timer", true)

        fun schedule(message: IMessage, time: Long, originUnit: TimeUnit) {
            schedule(message, TimeUnit.MILLISECONDS.convert(time, originUnit))
        }

        fun schedule(message: IMessage, nanos: Long = 60000L) {
            timer.schedule(newMessageDeletionTask(message), nanos)
        }

        fun IMessage.deleteIn(nanos: Long = 60000L) {
            timer.schedule(newMessageDeletionTask(this), nanos)
        }

        private fun newMessageDeletionTask(message: IMessage): TimerTask {
            return object : TimerTask() {
                override fun run() {
                    if (!message.isDeleted) {
                        message.delete()
                    }
                }
            }
        }

        val logger = LogManager.getLogger()
    }
}
