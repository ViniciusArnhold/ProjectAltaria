package me.viniciusarnhold.altaria.utils.configuration

import org.apache.commons.configuration2.Configuration
import org.apache.commons.configuration2.FileBasedConfiguration
import org.apache.commons.configuration2.PropertiesConfiguration
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder
import org.apache.commons.configuration2.builder.fluent.Parameters
import org.apache.commons.configuration2.ex.ConfigurationException
import org.apache.commons.configuration2.sync.LockMode
import org.apache.logging.log4j.LogManager

object ConfigurationManager {

    private var config: Configuration? = null

    init {
        loadConfigurationManager()
    }

    private fun loadConfigurationManager() {
        log.trace("Initializing configuration cache.")

        val params = Parameters()

        val builder = FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration::class.java)
                .configure(params.properties()
                        .setFileName(PROPERTIES_FILE))

        try {
            this.config = builder.configuration
        } catch (e: ConfigurationException) {
            log.fatal(e)
            throw RuntimeException("Couldnt load configuration file " + PROPERTIES_FILE)
        }

    }

    class Configurations<T>(val keyName: String, private var actualValue: T, private val type: Class<T>) {
        private val defaultValue: T = actualValue

        init {
            ensureCached()
        }

        private fun ensureCached() {
            val config = ConfigurationManager.config!!

            config.lock(LockMode.READ)

            populate(config)

            config.unlock(LockMode.READ)
        }

        fun key(): String {
            return this.keyName
        }

        fun type(): Class<T> {
            return this.type
        }

        fun defaultValue(): T {
            return this.defaultValue
        }

        fun value(): T {
            return this.actualValue
        }

        fun setValue(actualValue: T): T {
            this.actualValue = actualValue
            return this.actualValue
        }

        private fun populate(config: Configuration) {
            setValue(config.get(type(), key(), defaultValue()))
        }

        companion object {
            val ClientID = Configurations("client.id", -1L, Long::class.java)
            val ClientSecret = Configurations("client.secret", "", String::class.java)
            val BotToken = Configurations("bot.token", "", String::class.java)
            val LeagueOauthToken = Configurations("bot.apis.league.oauth.token", "", String::class.java)
            val LeagueAPIMode = Configurations("bot.apis.league.config.async", true, Boolean::class.java)
            val GoogleAPIToken = Configurations("bot.apis.google.api.token", "", String::class.java)
            val GoogleClientID = Configurations("bot.apis.google.client.id", "", String::class.java)
            val GoogleClientSecret = Configurations("bot.apis.google.client.secret", "", String::class.java)
        }
    }

    const val PROPERTIES_FILE = "properties/bot.properties"

    private val log = LogManager.getLogger(ConfigurationManager::class.java)
}
