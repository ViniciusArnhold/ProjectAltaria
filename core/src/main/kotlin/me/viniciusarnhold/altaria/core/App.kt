package me.viniciusarnhold.altaria.core

import com.diffplug.common.base.DurianPlugins
import com.diffplug.common.base.Errors
import me.viniciusarnhold.altaria.utils.Logs
import org.apache.logging.log4j.LogManager
import sx.blah.discord.util.DiscordException

/**
 * Main class.

 * @since 1.0
 */
object App {

    @Throws(DiscordException::class)
    @JvmStatic fun execute(args: Array<String>): ExecutionResult {

        try {
            //App configuration
            val logger = LogManager.getLogger()

            //Configuration third partys
            DurianPlugins.register(Errors.Plugins.Log::class.java, Errors.Plugins.Log { Logs.forThrowable(it) })

            logger.traceEntry()

            BotManager.instance.start()

            logger.traceExit()
        } catch (e: Exception) {
            return ExecutionResult(1, e)
        }
        return ExecutionResult(0)
    }
}

class ExecutionResult(val statusCode: Int, val error: Exception? = null) {
    /**
     *@author Vincius Pegorini Arnhold
     */
    fun isFailure() = statusCode != 0

    fun shouldRestart() = statusCode > 0
}
