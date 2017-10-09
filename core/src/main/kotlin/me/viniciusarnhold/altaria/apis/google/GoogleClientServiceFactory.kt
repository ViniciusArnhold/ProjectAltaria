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
import me.viniciusarnhold.altaria.utils.Timers
import me.viniciusarnhold.altaria.utils.configuration.ConfigurationManager.Configurations.Companion.GoogleAPIToken
import me.viniciusarnhold.altaria.utils.configuration.ConfigurationManager.Configurations.Companion.GoogleClientID
import me.viniciusarnhold.altaria.utils.configuration.ConfigurationManager.Configurations.Companion.GoogleClientSecret
import org.apache.logging.log4j.LogManager
import java.io.IOException
import java.lang.RuntimeException
import java.security.GeneralSecurityException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

/**
 * Created by Vinicius.

 * @since ${PROJECT_VERSION}
 */
object GoogleClientServiceFactory {

    private val cacheClients: ConcurrentHashMap<Class<out AbstractGoogleJsonClient>, AbstractGoogleJsonClient> = ConcurrentHashMap<Class<out AbstractGoogleJsonClient>, AbstractGoogleJsonClient>()

    val scriptService: Script
        get() {
            val service = cacheClients.computeIfAbsent(Script::class.java
            ) { clazz ->
                Script.Builder(
                        secureHttpTransport(),
                        JacksonFactory.getDefaultInstance(),
                        credential)

                        .setApplicationName(EventManager.instance.name)
                        .setScriptRequestInitializer(ScriptRequestInitializer(GoogleAPIToken.value()))
                        .setGoogleClientRequestInitializer(ScriptRequestInitializer(GoogleAPIToken.value()))
                        .setHttpRequestInitializer(createHttpTimeout(credential, 380000))
                        .build()
            } as Script

            Timers.CacheCleanUpService.schedule(cacheClients, Script::class.java, service, 30, TimeUnit.MINUTES)
            return service
        }


    val sheetsService: Sheets
        get() {
            val service = cacheClients.computeIfAbsent(Sheets::class.java
            ) { clazz ->
                Sheets.Builder(
                        secureHttpTransport(),
                        JacksonFactory.getDefaultInstance(),
                        credential)

                        .setApplicationName(EventManager.instance.name)
                        .setSheetsRequestInitializer(SheetsRequestInitializer(GoogleAPIToken.value()))
                        .setGoogleClientRequestInitializer(SheetsRequestInitializer(GoogleAPIToken.value()))
                        .build()
            } as Sheets

            Timers.CacheCleanUpService.schedule(cacheClients, Sheets::class.java, service, 30, TimeUnit.MINUTES)
            return service
        }

    val urlShortenerService: Urlshortener
        get() {
            val service = cacheClients.computeIfAbsent(Urlshortener::class.java
            ) {
                Urlshortener.Builder(
                        secureHttpTransport(),
                        JacksonFactory.getDefaultInstance(),
                        credential)

                        .setApplicationName(EventManager.instance.name)
                        .setUrlshortenerRequestInitializer(UrlshortenerRequestInitializer(GoogleAPIToken.value()))
                        .setGoogleClientRequestInitializer(UrlshortenerRequestInitializer(GoogleAPIToken.value()))
                        .build()
            } as Urlshortener

            Timers.CacheCleanUpService.schedule(cacheClients, Urlshortener::class.java, service, 30, TimeUnit.MINUTES)
            return service
        }

    val surveyService: Surveys
        get() {
            val service = cacheClients.computeIfAbsent(Surveys::class.java
            ) {
                Surveys.Builder(
                        secureHttpTransport(),
                        JacksonFactory.getDefaultInstance(),
                        credential)

                        .setApplicationName(EventManager.instance.name)
                        .setSurveysRequestInitializer(SurveysRequestInitializer(GoogleAPIToken.value()))
                        .setGoogleClientRequestInitializer(SurveysRequestInitializer(GoogleAPIToken.value()))
                        .build()
            } as Surveys

            Timers.CacheCleanUpService.schedule(cacheClients, Urlshortener::class.java, service, 30, TimeUnit.MINUTES)
            return service
        }

    //Will not occur
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
        googleSecrets.installed = GoogleClientSecrets.Details()
                .setClientId(GoogleClientID.value())
                .setClientSecret(GoogleClientSecret.value())
    }

    private lateinit var googleCredential: Credential

}
