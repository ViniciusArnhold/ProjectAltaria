package me.viniciusarnhold.altaria.utils;

import me.viniciusarnhold.altaria.enums.ConfigKey;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * Created by Vinicius on 1/30/2017.
 */
public class ConfigReader extends PropertiesConfiguration {

    public ConfigReader(String pathToPropeties) throws ConfigurationException {
        super(pathToPropeties);
    }

    public String getString(ConfigKey key) {
        return this.getString(key.key(), (String) key.defaultValue());
    }

    public Long getLong(ConfigKey key) {
        return this.getLong(key.key(), (Long) key.defaultValue());
    }

    public Integer getInteger(ConfigKey key) {
        return this.getInteger(key.key(), (Integer) key.defaultValue());
    }
}
