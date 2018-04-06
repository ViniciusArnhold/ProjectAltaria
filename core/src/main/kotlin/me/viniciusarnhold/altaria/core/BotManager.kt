package me.viniciusarnhold.altaria.core

import me.viniciusarnhold.altaria.events.EventManager
import me.viniciusarnhold.altaria.exceptions.BotLoginFailedException
import me.viniciusarnhold.altaria.utils.configuration.APIConfiguration
import me.viniciusarnhold.altaria.utils.configuration.ConfigurationManager
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import sx.blah.discord.api.ClientBuilder
import sx.blah.discord.api.IDiscordClient
import sx.blah.discord.handle.obj.Permissions
import sx.blah.discord.util.BotInviteBuilder
import sx.blah.discord.util.DiscordException
import java.nio.file.Paths
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

        val config = ConfigurationManager(APIConfiguration.fromFile(Paths.get(System.getenv("ALTARIA_BOT_PROPERTIES") ?: throw Exception("Env ``ALTARIA_BOT_PROPERTIES`` not set"))))
        ConfigurationManager.currentInstance = config

        val client: IDiscordClient
        try {
            client = ClientBuilder()
                    .withToken(config.config.bot.token)
                    .setDaemon(false)
                    .login()
        } catch (e: DiscordException) {
            LOGGER.fatal("Fail to login to discord.", e)
            throw BotLoginFailedException(e)
        }

        this.discordClient = client
        this.discordClient!!.shards.forEach { s -> s.streaming("!alt help", "https://github.com/ViniciusArnhold/ProjectAltaria") }

        this.invite = BotInviteBuilder(discordClient)
                .withClientID(config.config.client.id)
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
        if (discordClient == null) {
            LOGGER.warn("Method close called on 'null' discordClient")
            return
        }
        LOGGER.warn(" Disconecting from Discord.")
        discordClient!!.logout()
    }

    companion object {
        val LOGGER: Logger = LogManager.getLogger()

        val BOT_NAME = "AltariaBot"
        val REPO_URL = "https://github.com/ViniciusArnhold/ProjectAltaria"
        val instance = BotManager()
    }
}
