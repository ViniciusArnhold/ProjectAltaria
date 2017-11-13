package me.viniciusarnhold.altaria.utils.configuration

import java.nio.file.Paths

object ConfigurationManager {

    class Configurations {
        companion object {
            private val config = APIConfiguration.fromFile(Paths.get("./bot.properties"))

            val ClientID = config.client.id
            val ClientSecret = config.client.secret
            val BotToken = config.bot.token
            val LeagueOauthToken = config.bot.api.league.token
            val LeagueAPIMode = config.bot.api.league.async
            val GoogleAPIToken = config.bot.api.google.token
            val GoogleClientID = config.bot.api.google.id
            val GoogleClientSecret = config.bot.api.google.secret
        }
    }
}
