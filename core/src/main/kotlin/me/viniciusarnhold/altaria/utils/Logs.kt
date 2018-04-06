package me.viniciusarnhold.altaria.utils

import me.viniciusarnhold.altaria.core.App
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager

/**
 * Created by Vinicius.

 * @since 1.0
 */
object Logs {

    private val logger = LogManager.getLogger(App::class.java)

    fun forThrowable(t: Throwable) {
        logger.error(t)
    }

    fun forMessage(level: Level, message: String) {
        logger.log(level, message)
    }

    fun forMessage(message: String) {
        logger.log(Level.ERROR, message)
    }

}
