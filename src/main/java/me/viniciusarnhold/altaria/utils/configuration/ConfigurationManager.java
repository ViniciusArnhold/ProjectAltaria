package me.viniciusarnhold.altaria.utils.configuration;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.sync.LockMode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class ConfigurationManager {

    private static final String PROPERTIES_FILE = "properties/bot.properties";

    private static final Logger log = LogManager.getLogger(ConfigurationManager.class);

    @NotNull
    private static ConfigurationManager ourInstance = new ConfigurationManager();

    private Configuration config;

    private ConfigurationManager() {
        loadConfigurationManager();
    }

    @NotNull
    public static ConfigurationManager getInstance() {
        return ourInstance;
    }

    private void loadConfigurationManager() {
        log.trace(() -> "Initializing configuration cache.");

        @NotNull Parameters params = new Parameters();

        FileBasedConfigurationBuilder<FileBasedConfiguration> builder =
                new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
                        .configure(params.properties()
                                .setFileName(PROPERTIES_FILE));

        try {
            this.config = builder.getConfiguration();
        } catch (ConfigurationException e) {
            log.fatal(e);
            throw new RuntimeException("Couldnt load configuration file " + PROPERTIES_FILE);
        }
    }

    public final Configuration configuration() {
        return this.config;
    }

    public static final class Configurations<T> {

        public static final Configurations<Long> ClientID = new Configurations<>("client.id", -1L, Long.class);
        public static final Configurations<String> ClientSecret = new Configurations<>("client.secret", "", String.class);
        public static final Configurations<String> BotToken = new Configurations<>("bot.token", "", String.class);
        public static final Configurations<String> LeagueOauthToken = new Configurations<>("bot.apis.league.oauth.token", "", String.class);
        public static final Configurations<Boolean> LeagueAPIMode = new Configurations<>("bot.apis.league.config.async", true, Boolean.class);
        public static final Configurations<String> GoogleAPIToken = new Configurations<>("bot.apis.google.api.token", "", String.class);
        public static final Configurations<String> GoogleClientID = new Configurations<>("bot.apis.google.client.id", "", String.class);
        public static final Configurations<String> GoogleClientSecret = new Configurations<>("bot.apis.google.client.secret", "", String.class);

        private final String keyName;

        private final T defaultValue;
        private final Class<T> type;
        private T actualValue;

        public Configurations(@NotNull String keyName, @NotNull T defaultValue, @NotNull Class<T> type) {
            this.keyName = Objects.requireNonNull(keyName);
            this.defaultValue = Objects.requireNonNull(defaultValue);
            this.type = Objects.requireNonNull(type);
            this.actualValue = defaultValue;
            ensureCached();
        }

        private void ensureCached() {
            final Configuration config = ConfigurationManager.getInstance().configuration();

            config.lock(LockMode.READ);

            populate(config);

            config.unlock(LockMode.READ);
        }

        public String key() {
            return this.keyName;
        }

        public Class<T> type() {
            return this.type;
        }

        public T defaultValue() {
            return this.defaultValue;
        }

        public T value() {
            return this.actualValue;
        }

        public final T setValue(T actualValue) {
            return this.actualValue = actualValue;
        }

        private void populate(@NotNull Configuration config) {
            setValue(config.get(type(), key(), defaultValue()));
        }
    }
}
