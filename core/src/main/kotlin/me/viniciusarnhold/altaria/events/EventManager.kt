package me.viniciusarnhold.altaria.events

import me.viniciusarnhold.altaria.command.common.*
import me.viniciusarnhold.altaria.command.external.imdb.ImdbSearchCommand
import me.viniciusarnhold.altaria.command.pool.PoolCommand
import me.viniciusarnhold.altaria.command.random.XKCDCommand
import me.viniciusarnhold.altaria.events.interfaces.IReceiver
import me.viniciusarnhold.altaria.events.receivers.MessageReceivedEventReceiver
import org.apache.logging.log4j.LogManager
import sx.blah.discord.api.IDiscordClient
import sx.blah.discord.api.events.Event
import sx.blah.discord.api.events.IListener
import sx.blah.discord.modules.IModule
import java.util.*

/**
 * Created by Vinicius.

 * @since ${PROJECT_VERSION}
 */
class EventManager : IModule {

    override fun enable(client: IDiscordClient): Boolean {
        discordClient = client


        discordClient!!.dispatcher.registerListener(PoolCommand.instance)
        discordClient!!.dispatcher.registerListener(InviteCommand())
        discordClient!!.dispatcher.registerListener(XKCDCommand())
        discordClient!!.dispatcher.registerListener(PingCommand())
        discordClient!!.dispatcher.registerListener(UptimeCommand())
        discordClient!!.dispatcher.registerListener(EigthBallCommand())
        discordClient!!.dispatcher.registerListener(RandomNumberCommand())
        discordClient!!.dispatcher.registerListener(ImdbSearchCommand())

        registerListener<MessageReceivedEventReceiver>(MessageReceivedEventReceiver.instace)

        return true
    }

    fun <T> registerListener(eventListener: T) where T : IReceiver, T : IListener<out Event> {
        discordClient!!.dispatcher.registerListener(eventListener)
        eventListeners.add(eventListener)
    }

    override fun disable() {
        var e: Exception? = null
        for (receiver in eventListeners) {
            try {
                receiver.disable()
            } catch (ex: Exception) {
                logger.warn("Exception on disable call to receiver " + receiver.javaClass.simpleName, ex)
                e = ex
            }

        }
        if (e != null) {
            throw RuntimeException(e)
        }
    }

    override fun getName(): String {
        return moduleName
    }

    override fun getAuthor(): String {
        return author
    }

    override fun getVersion(): String {
        return moduleVersion
    }

    override fun getMinimumDiscord4JVersion(): String {
        return moduleMinimumVersion
    }

    companion object {

        val MAIN_COMMAND_NAME = "!alt"

        private val logger = LogManager.getLogger()
        val instance = EventManager()
        private val eventListeners = HashSet<IReceiver>()
        private val moduleName = "Project Altaria"
        private val moduleVersion = "1.0"
        private val moduleMinimumVersion = "2.6"
        private val author = "Vinicius Pegorini Arnhold"
        var discordClient: IDiscordClient? = null
            private set
    }
}
