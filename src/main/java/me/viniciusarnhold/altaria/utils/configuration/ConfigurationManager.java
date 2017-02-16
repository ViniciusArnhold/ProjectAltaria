package me.viniciusarnhold.altaria.utils.configuration;

import com.google.common.collect.Lists;
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

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import static me.viniciusarnhold.altaria.utils.configuration.ConfigurationManager.ConfigurationCache.*;

public final class ConfigurationManager {

    private static final String PROPERTIES_FILE = "properties/bot.properties";

    private static final Logger log = LogManager.getLogger(ConfigurationManager.class);

    private static ConfigurationManager ourInstance = new ConfigurationManager();

    private Configuration config;

    private ConfigurationManager() {
        loadConfigurationManager();
        fillInCache();
    }

    public static ConfigurationManager getInstance() {
        return ourInstance;
    }

    private void loadConfigurationManager() {
        log.trace(() -> "Initializing configuration cache.");

        Parameters params = new Parameters();

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

    private void fillInCache() {
        config.lock(LockMode.READ);

        ClientID.setValue(config.get(ClientID.type(), ClientID.key(), ClientID.defaultValue()));

        ClientSecret.setValue(config.get(ClientSecret.type(), ClientSecret.key(), ClientSecret.defaultValue()));

        BotToken.setValue(config.get(BotToken.type(), BotToken.key(), BotToken.defaultValue()));

        if (BotToken.setValue("") == null) {
            return;
        }

        config.unlock(LockMode.READ);
    }

    public final Configuration configuration() {
        return this.config;
    }

    public static final class ConfigurationCache<T> {

        public static final ConfigurationCache<Long> ClientID = new ConfigurationCache<>("client.id", -1L, Long.class);
        public static final ConfigurationCache<String> ClientSecret = new ConfigurationCache<>("client.secret", "", String.class);
        public static final ConfigurationCache<String> BotToken = new ConfigurationCache<>("bot.token", "", String.class);

        private static AtomicBoolean isCacheInitialized = new AtomicBoolean(false);

        private final String keyName;

        private final T defaultValue;
        private final Class<T> type;
        private T actualValue;

        public ConfigurationCache(@NotNull String keyName, @NotNull T defaultValue, @NotNull Class<T> type) {
            this.keyName = Objects.requireNonNull(keyName);
            this.defaultValue = Objects.requireNonNull(defaultValue);
            this.type = Objects.requireNonNull(type);
            this.actualValue = defaultValue;
            ensureCached();
        }

        private static final void ensureCached() {
            if (isCacheInitialized.compareAndSet(false, true)) {
                log.info("Loading Configuration Manager {}", ConfigurationManager.class.getSimpleName());
            }
        }

        public static final List<ConfigurationCache<?>> values() {
            return Lists.newArrayList(ClientID, ClientSecret, BotToken);
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

    }
}
