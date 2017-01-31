package me.viniciusarnhold.altaria.core;

import me.viniciusarnhold.altaria.enums.ConfigKey;
import me.viniciusarnhold.altaria.utils.ConfigReader;
import org.apache.commons.configuration.ConfigurationException;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RateLimitException;

import java.util.Objects;

/**
 * Hello world!
 */
public class App {

    public static void main(String[] args) throws DiscordException {

        //Read Config files
        Long clientID = null;
        String clientSecret = null;
        String clientToken = null;

        try {
            ConfigReader config = new ConfigReader("keys.bot.properties");
            clientID = config.getLong(ConfigKey.ClientID);
            clientSecret = config.getString(ConfigKey.ClientSecret);
            clientToken = config.getString(ConfigKey.BotToken);

        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
        Objects.requireNonNull(clientID, () -> "Failed to read property" + ConfigKey.ClientID.key());
        Objects.requireNonNull(clientSecret, () -> "Failed to read property" + ConfigKey.ClientSecret.key());

        //Build Discord Client

        IDiscordClient discordClient =
                new ClientBuilder()
                        .withToken(clientToken)
                        .login();

        System.out.println(discordClient.getApplicationName());
        System.out.println(discordClient.getApplicationOwner());
        System.out.println(discordClient.getDescription());
        System.out.println(discordClient.getMessages());

        try {
            discordClient.logout();
        } catch (RateLimitException e) {
            e.printStackTrace();
        }

    }
}
