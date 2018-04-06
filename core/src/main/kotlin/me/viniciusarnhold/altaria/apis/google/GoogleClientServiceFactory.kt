package me.viniciusarnhold.altaria.apis.google

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient
import com.google.api.client.http.HttpRequest
import com.google.api.client.http.HttpRequestInitializer
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.MemoryDataStoreFactory
import com.google.api.services.script.Script
import com.google.api.services.script.ScriptRequestInitializer
import com.google.api.services.script.ScriptScopes
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsRequestInitializer
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.api.services.surveys.Surveys
import com.google.api.services.surveys.SurveysRequestInitializer
import com.google.api.services.surveys.SurveysScopes
import com.google.api.services.urlshortener.Urlshortener
import com.google.api.services.urlshortener.UrlshortenerRequestInitializer
import com.google.api.services.urlshortener.UrlshortenerScopes
import me.viniciusarnhold.altaria.events.EventManager
import me.viniciusarnhold.altaria.utils.configuration.ConfigurationManager
import org.apache.logging.log4j.LogManager
import java.io.IOException
import java.lang.RuntimeException
import java.security.GeneralSecurityException
import java.util.concurrent.ConcurrentHashMap

/**
 * Created by Vinicius.

 * @since 1.0
 */
object GoogleClientServiceFactory {

    private val cacheClients: ConcurrentHashMap<Class<out AbstractGoogleJsonClient>, AbstractGoogleJsonClient> = ConcurrentHashMap<Class<out AbstractGoogleJsonClient>, AbstractGoogleJsonClient>()

    val scriptService: Script
        get() {
            val service = cacheClients.computeIfAbsent(Script::class.java
            ) { clazz ->
                //FIXME Null assert
                val token = ConfigurationManager.currentInstance!!.config.bot.api.google.token

                Script.Builder(
                        secureHttpTransport(),
                        JacksonFactory.getDefaultInstance(),
                        credential)

                        .setApplicationName(EventManager.instance.name)
                        .setScriptRequestInitializer(ScriptRequestInitializer(token))
                        .setGoogleClientRequestInitializer(ScriptRequestInitializer(token))
                        .setHttpRequestInitializer(createHttpTimeout(credential, 380000))
                        .build()
            } as Script

            return service
        }


    val sheetsService: Sheets
        get() {
            val service = cacheClients.computeIfAbsent(Sheets::class.java
            ) { clazz ->
                //FIXME Null assert
                val token = ConfigurationManager.currentInstance!!.config.bot.api.google.token

                Sheets.Builder(secureHttpTransport(), JacksonFactory.getDefaultInstance(), credential)
                        .setApplicationName(EventManager.instance.name)
                        .setSheetsRequestInitializer(SheetsRequestInitializer(token))
                        .setGoogleClientRequestInitializer(SheetsRequestInitializer(token))
                        .build()
            } as Sheets

            return service
        }

    val urlShortenerService: Urlshortener
        get() {
            //FIXME Null assert
            val token = ConfigurationManager.currentInstance!!.config.bot.api.google.token

            val service = cacheClients.computeIfAbsent(Urlshortener::class.java
            ) {
                Urlshortener.Builder(
                        secureHttpTransport(),
                        JacksonFactory.getDefaultInstance(),
                        credential)

                        .setApplicationName("altaria-bot")
                        .setUrlshortenerRequestInitializer(UrlshortenerRequestInitializer(token))
                        .setGoogleClientRequestInitializer(UrlshortenerRequestInitializer(token))
                        .build()
            } as Urlshortener

            return service
        }

    val surveyService: Surveys
        get() {
            //FIXME Null assert
            val token = ConfigurationManager.currentInstance!!.config.bot.api.google.token

            val service = cacheClients.computeIfAbsent(Surveys::class.java
            ) {
                Surveys.Builder(
                        secureHttpTransport(),
                        JacksonFactory.getDefaultInstance(),
                        credential)

                        .setApplicationName(EventManager.instance.name)
                        .setSurveysRequestInitializer(SurveysRequestInitializer(token))
                        .setGoogleClientRequestInitializer(SurveysRequestInitializer(token))
                        .build()
            } as Surveys

            return service
        }

    val credential: Credential by lazy {
        try {
            val flow = GoogleAuthorizationCodeFlow.Builder(
                    secureHttpTransport,
                    JacksonFactory.getDefaultInstance(),
                    googleSecrets,
                    requiredScopes)
                    .setAccessType("offline")
                    .setDataStoreFactory(MemoryDataStoreFactory())
                    .build()

            googleCredential = AuthorizationCodeInstalledApp(flow, LocalServerReceiver())
                    .authorize("user")

        } catch (e: IOException) {
            logger.fatal(e)
            throw RuntimeException()
        }
        googleCredential
    }

    fun createHttpTimeout(requestInitializer: HttpRequestInitializer, value: Int): HttpRequestInitializer {
        //As seeon on: https://developers.google.com/apps-script/guides/rest/quickstart/java
        return HttpRequestInitializer { httpRequest: HttpRequest ->
            requestInitializer.initialize(httpRequest)
            // This allows the API to call (and avoid timing out on)
            // functions that take up to 6 minutes to complete (the maximum
            // allowed script run time), plus a little overhead.
            httpRequest.readTimeout = value
        }
    }

    private fun secureHttpTransport(): HttpTransport {
        return secureHttpTransport
    }

    private val logger = LogManager.getLogger()

    private val secureHttpTransport: HttpTransport = try {
        GoogleNetHttpTransport.newTrustedTransport()
    } catch (e: GeneralSecurityException) {
        logger.fatal("Failed to generate Secure Google HTTP Transport.", e)
        throw RuntimeException()
    } catch (e: IOException) {
        logger.fatal("Failed to generate Secure Google HTTP Transport.", e)
        throw RuntimeException()
    }

    private val googleSecrets = GoogleClientSecrets()

    private val requiredScopes: Set<String> = setOf(ScriptScopes.all(), SheetsScopes.all(), SurveysScopes.all(), UrlshortenerScopes.all()).flatMap { it.asIterable() }.filterNotNull().toSet()

    init {
        //FIXME Null assert
        val googleConfig = ConfigurationManager.currentInstance!!.config.bot.api.google

        googleSecrets.installed = GoogleClientSecrets.Details()
                .setClientId(googleConfig.id)
                .setClientSecret(googleConfig.secret)
    }

    private lateinit var googleCredential: Credential

}
