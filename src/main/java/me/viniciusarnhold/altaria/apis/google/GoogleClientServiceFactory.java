package me.viniciusarnhold.altaria.apis.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.MemoryDataStoreFactory;
import com.google.api.services.script.Script;
import com.google.api.services.script.ScriptRequestInitializer;
import com.google.api.services.script.ScriptScopes;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsRequestInitializer;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.surveys.Surveys;
import com.google.api.services.surveys.SurveysRequestInitializer;
import com.google.api.services.surveys.SurveysScopes;
import com.google.api.services.urlshortener.Urlshortener;
import com.google.api.services.urlshortener.UrlshortenerRequestInitializer;
import com.google.api.services.urlshortener.UrlshortenerScopes;
import com.google.common.collect.ImmutableSet;
import me.viniciusarnhold.altaria.events.EventManager;
import me.viniciusarnhold.altaria.utils.Timers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static me.viniciusarnhold.altaria.utils.configuration.ConfigurationManager.Configurations.*;

/**
 * Created by Vinicius.
 *
 * @since ${PROJECT_VERSION}
 */
public class GoogleClientServiceFactory {

    private static final Logger logger = LogManager.getLogger();

    private static final GoogleClientServiceFactory ourInstance = new GoogleClientServiceFactory();

    @NotNull
    private static final HttpTransport secureHttpTransport;

    private static final GoogleClientSecrets googleSecrets = new GoogleClientSecrets();

    private static final Set<String> requiredScopes;

    private static Credential googleCredential;

    static {
        HashSet<String> set = new HashSet<>();
        set.addAll(ScriptScopes.all());
        set.addAll(SheetsScopes.all());
        set.addAll(SurveysScopes.all());
        set.addAll(UrlshortenerScopes.all());
        requiredScopes = ImmutableSet.copyOf(set);
    }

    static {
        try {
            secureHttpTransport = GoogleNetHttpTransport.newTrustedTransport();
        } catch (@NotNull GeneralSecurityException | IOException e) {
            logger.fatal("Failed to generate Secure Google HTTP Transport.", e);
            throw new RuntimeException();
        }
        googleSecrets.setInstalled(
                new GoogleClientSecrets.Details()
                        .setClientId(GoogleClientID.value())
                        .setClientSecret(GoogleClientSecret.value())
                                  );
    }

    @NotNull
    private final ConcurrentHashMap<Class<? extends AbstractGoogleJsonClient>, AbstractGoogleJsonClient> cacheClients;

    private GoogleClientServiceFactory() {
        cacheClients = new ConcurrentHashMap<>();
    }

    @NotNull
    public static final GoogleClientServiceFactory getInstance() {
        return ourInstance;
    }

    @NotNull
    private static final HttpTransport secureHttpTransport() {
        return secureHttpTransport;
    }

    @NotNull
    public final Script getScriptService() {
        Script service = (Script) cacheClients.computeIfAbsent(Script.class,
                (clazz) ->
                        new Script.Builder(
                                secureHttpTransport(),
                                JacksonFactory.getDefaultInstance(),
                                getCredential())

                                .setApplicationName(EventManager.getInstance().getName())
                                .setScriptRequestInitializer(new ScriptRequestInitializer(GoogleAPIToken.value()))
                                .setGoogleClientRequestInitializer(new ScriptRequestInitializer(GoogleAPIToken.value()))
                                .setHttpRequestInitializer(createHttpTimeout(getCredential(), 380000))
                                .build());

        Timers.cacheCleanUpTimer().schedule(cacheClients, Script.class, service, 30, TimeUnit.MINUTES);
        return service;
    }


    @NotNull
    public final Sheets getSheetsService() {
        Sheets service = (Sheets) cacheClients.computeIfAbsent(Sheets.class,
                (clazz) ->
                        new Sheets.Builder(
                                secureHttpTransport(),
                                JacksonFactory.getDefaultInstance(),
                                getCredential())

                                .setApplicationName(EventManager.getInstance().getName())
                                .setSheetsRequestInitializer(new SheetsRequestInitializer(GoogleAPIToken.value()))
                                .setGoogleClientRequestInitializer(new SheetsRequestInitializer(GoogleAPIToken.value()))
                                .build());

        Timers.cacheCleanUpTimer().schedule(cacheClients, Sheets.class, service, 30, TimeUnit.MINUTES);
        return service;
    }

    @NotNull
    public final Urlshortener getUrlShortenerService() {
        Urlshortener service = (Urlshortener) cacheClients.computeIfAbsent(Urlshortener.class,
                (clazz) ->
                        new Urlshortener.Builder(
                                secureHttpTransport(),
                                JacksonFactory.getDefaultInstance(),
                                getCredential())

                                .setApplicationName(EventManager.getInstance().getName())
                                .setUrlshortenerRequestInitializer(new UrlshortenerRequestInitializer(GoogleAPIToken.value()))
                                .setGoogleClientRequestInitializer(new UrlshortenerRequestInitializer(GoogleAPIToken.value()))
                                .build());

        Timers.cacheCleanUpTimer().schedule(cacheClients, Urlshortener.class, service, 30, TimeUnit.MINUTES);
        return service;
    }

    @NotNull
    public final Surveys getSurveyService() {
        Surveys service = (Surveys) cacheClients.computeIfAbsent(Surveys.class,
                (clazz) ->
                        new Surveys.Builder(
                                secureHttpTransport(),
                                JacksonFactory.getDefaultInstance(),
                                getCredential())

                                .setApplicationName(EventManager.getInstance().getName())
                                .setSurveysRequestInitializer(new SurveysRequestInitializer(GoogleAPIToken.value()))
                                .setGoogleClientRequestInitializer(new SurveysRequestInitializer(GoogleAPIToken.value()))
                                .build());

        Timers.cacheCleanUpTimer().schedule(cacheClients, Urlshortener.class, service, 30, TimeUnit.MINUTES);
        return service;
    }

    public final Credential getCredential() {
        if (googleCredential == null) {
            try {
                GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                        secureHttpTransport,
                        JacksonFactory.getDefaultInstance(),
                        googleSecrets,
                        requiredScopes)

                        .setAccessType("offline")
                        .setDataStoreFactory(new MemoryDataStoreFactory())
                        .build();

                googleCredential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver())
                        .authorize("user");

            } catch (IOException e) {
                //Will not occur
                logger.fatal(e);
                throw new RuntimeException();
            }
        }
        return googleCredential;
    }

    public final HttpRequestInitializer createHttpTimeout(@NotNull final HttpRequestInitializer requestInitializer, final int value) {
        //As seeon on: https://developers.google.com/apps-script/guides/rest/quickstart/java
        return httpRequest -> {
            requestInitializer.initialize(httpRequest);
            // This allows the API to call (and avoid timing out on)
            // functions that take up to 6 minutes to complete (the maximum
            // allowed script run time), plus a little overhead.
            httpRequest.setReadTimeout(value);
        };
    }

}
