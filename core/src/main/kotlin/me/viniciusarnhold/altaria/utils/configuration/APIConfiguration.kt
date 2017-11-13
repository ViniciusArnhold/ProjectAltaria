package me.viniciusarnhold.altaria.utils.configuration

import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

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
            val props = Properties()
            props.load(Files.newBufferedReader(file))

            val apiConfig = mapper.readPropertiesAs<APIConfiguration>(props, APIConfiguration::class.java)

            return apiConfig
        }
    }

    class Client(
            val id: String,
            val secret: String
    )

    class Bot(val token: String,
              val api: Apis) {

        class Apis(val league: LeagueAPI, val google: GoogleAPI) {

            class LeagueAPI(val token: String, val async: Boolean)
            class GoogleAPI(
                    val token: String,
                    val id: String,
                    val secret: String)
        }
    }
}