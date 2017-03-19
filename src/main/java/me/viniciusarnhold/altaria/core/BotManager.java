package me.viniciusarnhold.altaria.core;

import me.viniciusarnhold.altaria.events.EventManager;
import me.viniciusarnhold.altaria.exceptions.BotLoginFailedException;
import me.viniciusarnhold.altaria.utils.configuration.ConfigurationManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.handle.obj.Status;
import sx.blah.discord.util.BotInviteBuilder;
import sx.blah.discord.util.DiscordException;

import java.text.MessageFormat;
import java.util.EnumSet;

/**
 * Created by Vinicius.
 *
 * @since 1.0
 */
public class BotManager implements AutoCloseable {

    public static final Logger LOGGER = LogManager.getLogger();

    public static final String EMAIL = "altaria.bot@gmail.com";
    public static final String BOT_NAME = "AltariaBot";
    public static final String REPO_URL = "https://github.com/ViniciusArnhold/ProjectAltaria";
    @NotNull
    private static BotManager ourInstance = new BotManager();
    private String invite;
    private IDiscordClient discordClient;

    private BotManager() {
    }

    @NotNull
    public static BotManager getInstance() {
        return ourInstance;
    }

    public final void start() {
        LOGGER.traceEntry();

        final IDiscordClient client;
        try {
            client = new ClientBuilder()
                    .withToken(ConfigurationManager.Configurations.BotToken.value())
                    .setDaemon(false)
                    .login();
        } catch (DiscordException e) {
            LOGGER.fatal("Fail to login to discord.", e);
            throw new BotLoginFailedException(e);
        }
        this.discordClient = client;
        this.discordClient.getShards().forEach(s -> s.changeStatus(Status.game("IntelliJ IDEA")));

        this.invite = new BotInviteBuilder(discordClient)
                .withClientID(ConfigurationManager.Configurations.ClientID.value().toString())
                .withPermissions(EnumSet.of(Permissions.ADMINISTRATOR))
                .build();
        System.out.println(MessageFormat.format("Bot Created, invitation link: {0}", invite));


        addEventManager();

        LOGGER.traceExit();
    }

    private void addEventManager() {

        discordClient.getModuleLoader().loadModule(new EventManager());

    }

    public String inviteUrl() {
        return this.invite;
    }


    @Override
    public void close() throws Exception {
        LOGGER.traceEntry();
        if (discordClient == null) {
            LOGGER.warn("Method close called on 'null' discordClient");
            return;
        }
        LOGGER.warn(" Disconecting from Discord.");
        discordClient.logout();

        LOGGER.traceExit();
    }
}
