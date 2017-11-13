package me.viniciusarnhold.altaria.core

import me.viniciusarnhold.altaria.events.EventManager
import me.viniciusarnhold.altaria.exceptions.BotLoginFailedException
import me.viniciusarnhold.altaria.utils.configuration.ConfigurationManager
import org.apache.logging.log4j.LogManager
import sx.blah.discord.api.ClientBuilder
import sx.blah.discord.api.IDiscordClient
import sx.blah.discord.handle.obj.Permissions
import sx.blah.discord.handle.obj.Status
import sx.blah.discord.util.BotInviteBuilder
import sx.blah.discord.util.DiscordException
import java.text.MessageFormat
import java.util.*

/**
 * Created by Vinicius.

 * @since 1.0
 */
class BotManager private constructor() : AutoCloseable {
    private lateinit var invite: String
    private var discordClient: IDiscordClient? = null

    fun start() {
        LOGGER.traceEntry()

        val client: IDiscordClient
        try {
            client = ClientBuilder()
                    .withToken(ConfigurationManager.Configurations.BotToken)
                    .setDaemon(false)
                    .login()
        } catch (e: DiscordException) {
            LOGGER.fatal("Fail to login to discord.", e)
            throw BotLoginFailedException(e)
        }

        this.discordClient = client
        this.discordClient!!.shards.forEach { s -> s.streaming("!alt help", "https://github.com/ViniciusArnhold/ProjectAltaria") }

        this.invite = BotInviteBuilder(discordClient)
                .withClientID(ConfigurationManager.Configurations.ClientID)
                .withPermissions(EnumSet.of(Permissions.ADMINISTRATOR))
                .build()
        println(MessageFormat.format("Bot Created, invitation link: {0}", invite))


        addEventManager()

        LOGGER.traceExit()
    }

    private fun addEventManager() {

        discordClient!!.moduleLoader.loadModule(EventManager())

    }

    fun inviteUrl(): String {
        return this.invite
    }


    @Throws(Exception::class)
    override fun close() {
        LOGGER.traceEntry()
        if (discordClient == null) {
            LOGGER.warn("Method close called on 'null' discordClient")
            return
        }
        LOGGER.warn(" Disconecting from Discord.")
        discordClient!!.logout()

        LOGGER.traceExit()
    }

    companion object {

        val LOGGER = LogManager.getLogger()

        val EMAIL = "altaria.bot@gmail.com"
        val BOT_NAME = "AltariaBot"
        val REPO_URL = "https://github.com/ViniciusArnhold/ProjectAltaria"
        val instance = BotManager()
    }
}
