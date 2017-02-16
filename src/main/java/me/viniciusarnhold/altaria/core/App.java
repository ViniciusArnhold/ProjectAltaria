package me.viniciusarnhold.altaria.core;

import me.viniciusarnhold.altaria.utils.configuration.ConfigurationManager.ConfigurationCache;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RateLimitException;

/**
 * Hello world!
 */
public class App {

    public static void main(String[] args) throws DiscordException {

        //App configuration
        final Logger logger = LogManager.getLogger();
        //Build Discord Client

        IDiscordClient discordClient =
                new ClientBuilder()
                        .withToken(ConfigurationCache.BotToken.value())
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
