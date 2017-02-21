package me.viniciusarnhold.altaria.core;

import me.viniciusarnhold.altaria.events.EventManager;
import me.viniciusarnhold.altaria.excpetions.BotLoginFailedException;
import me.viniciusarnhold.altaria.utils.configuration.ConfigurationManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.BotInviteBuilder;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RateLimitException;

import java.text.MessageFormat;
import java.util.EnumSet;

/**
 * Created by Vinicius.
 *
 * @since 1.0
 */
public class BotManager implements AutoCloseable {

    private static final Logger logger = LogManager.getLogger();
    private static BotManager ourInstance = new BotManager();
    private IDiscordClient discordClient;

    private BotManager() {
    }

    public static BotManager getInstance() {
        return ourInstance;
    }

    public final void start() {
        final IDiscordClient client;
        try {
            client = new ClientBuilder()
                    .withToken(ConfigurationManager.Configurations.BotToken.value())
                    .setDaemon(false)
                    .login(false);
        } catch (DiscordException e) {
            logger.fatal("Fail to login to discord.", e);
            throw new BotLoginFailedException(e);
        }
        this.discordClient = client;

        String invitation = new BotInviteBuilder(discordClient)
                .withClientID(ConfigurationManager.Configurations.ClientID.value().toString())
                .withPermissions(EnumSet.of(Permissions.SEND_MESSAGES, Permissions.READ_MESSAGE_HISTORY, Permissions.READ_MESSAGES))
                .build();
        System.out.println(MessageFormat.format("Bot Created, invitation link: {0}", invitation));


        addEventManager();
    }

    private void addEventManager() {

        discordClient.getModuleLoader().loadModule(new EventManager());

    }

    @Override
    public void close() throws Exception {
        logger.traceEntry();
        if (discordClient == null) {
            logger.warn("Method close called on 'null' discordClient");
        } else {
            logger.warn(" Disconecting from Discord.");

            try {
                discordClient.logout();
            } catch (RateLimitException e) {
                logger.error("Erro in client logout", e);
            }
        }
    }
}
