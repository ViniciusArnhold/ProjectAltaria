package me.viniciusarnhold.altaria.enums;

/**
 * Created by Vinicius on 1/30/2017.
 */
public enum ConfigKey {

    /**
     * @see ConfigKey#ClientSecret
     */
    ClientID("client.id", null),

    /**
     *
     */
    ClientSecret("client.secret", null),
    BotToken("bot.token", null);

    private final String keyName;
    private final Object defaultValue;

    ConfigKey(String keyName, Object defaultValue) {
        this.keyName = keyName;
        this.defaultValue = defaultValue;
    }

    /**
     * @return
     */
    public String key() {
        return this.keyName;
    }

    public Object defaultValue() {
        return this.defaultValue;
    }

}
