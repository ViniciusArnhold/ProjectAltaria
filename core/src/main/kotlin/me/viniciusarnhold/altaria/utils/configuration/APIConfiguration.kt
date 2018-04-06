package me.viniciusarnhold.altaria.utils.configuration

import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.nio.file.Files
import java.nio.file.Path

/**
 * @author Vinicius Pegorini Arnhold.
 */
class APIConfiguration private constructor(val client: Client,
                                           val bot: Bot) : BaseConfiguration() {

    companion object {
        private val mapper: JavaPropsMapper by lazy {
            val mapper = JavaPropsMapper()
            mapper.registerKotlinModule()
            mapper
        }

        fun fromFile(file: Path): APIConfiguration {
            if (!Files.exists(file) || !Files.isRegularFile(file)) {
                throw IllegalArgumentException("Invalid file $file")
            }

            return mapper.readValue(Files.newBufferedReader(file), APIConfiguration::class.java)
        }
    }

    class Client(
            val id: String,
            val secret: String
    )

    class Bot(val token: String,
              val api: Apis,
              val command: Commands = Commands()) {

        class Commands(val stats: Stats = Stats()) {
            class Stats(val incrementWithError: Boolean = false)
        }

        class Apis(val league: LeagueAPI, val google: GoogleAPI) {

            class LeagueAPI(val token: String, val async: Boolean)
            class GoogleAPI(
                    val token: String,
                    val id: String,
                    val secret: String)
        }
    }
}