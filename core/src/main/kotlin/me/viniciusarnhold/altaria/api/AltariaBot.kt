package me.viniciusarnhold.altaria.api

import me.viniciusarnhold.altaria.utils.configuration.APIConfiguration
import sx.blah.discord.handle.obj.IGuild
import java.net.URL

abstract class AltariaBot {
    abstract val registeredCommands: Set<ICommand>
    abstract val inviteUrl: URL
    abstract fun configurationFor(guild: IGuild): CommandConfiguration
    abstract val globalConfiguration: APIConfiguration

    class Builder {
        private lateinit var apiKey: String

        fun withAPIKey(key: String): Builder {
            this.apiKey = key
            return this
        }

        fun buildAndLogin(): AltariaBot {
            throw TODO()
        }

        fun build(): AltariaBot {
            throw TODO()
        }
    }
}