package me.viniciusarnhold.altaria.events;

import me.viniciusarnhold.altaria.command.common.InviteCommand;
import me.viniciusarnhold.altaria.command.common.PingCommand;
import me.viniciusarnhold.altaria.command.common.UptimeCommand;
import me.viniciusarnhold.altaria.command.pool.PoolCommand;
import me.viniciusarnhold.altaria.command.random.XKCDCommand;
import me.viniciusarnhold.altaria.events.interfaces.IReceiver;
import me.viniciusarnhold.altaria.events.receivers.MessageReceivedEventReceiver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.Event;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.modules.IModule;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Vinicius.
 *
 * @since ${PROJECT_VERSION}
 */
public class EventManager implements IModule {

    public static final String MAIN_COMMAND_NAME = "!alt";

    private static final Logger logger = LogManager.getLogger();
    private static final EventManager ourInstance = new EventManager();
    private static final Set<IReceiver> eventListeners = new HashSet<>();
    private static final String moduleName = "Project Altaria";
    private static final String moduleVersion = "1.0";
    private static final String moduleMinimumVersion = "2.6";
    private static final String author = "Vinicius Pegorini Arnhold";
    private static IDiscordClient discordClient;

    public EventManager() {
    }

    @NotNull
    public static EventManager getInstance() {
        return ourInstance;
    }

    public static IDiscordClient getDiscordClient() {
        return discordClient;
    }

    @Override
    public boolean enable(IDiscordClient client) {
        discordClient = client;

        discordClient.getDispatcher().registerListener(PoolCommand.getInstance());
        discordClient.getDispatcher().registerListener(new InviteCommand());
        discordClient.getDispatcher().registerListener(new XKCDCommand());
        discordClient.getDispatcher().registerListener(new PingCommand());
        discordClient.getDispatcher().registerListener(new UptimeCommand());
        registerListener(MessageReceivedEventReceiver.getInstace());

        return true;
    }

    private <T extends IReceiver & IListener<? extends Event>> void registerListener(@NotNull T eventListener) {
        discordClient.getDispatcher().registerListener(eventListener);
        eventListeners.add(eventListener);
    }

    @Override
    public void disable() {
        Exception e = null;
        for (IReceiver receiver :
                eventListeners) {
            try {
                receiver.disable();
            } catch (Exception ex) {
                logger.warn("Exception on disable call to receiver " + receiver.getClass().getSimpleName(), ex);
                e = ex;
            }
        }
        if (e != null) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    @Override
    public String getName() {
        return moduleName;
    }

    @NotNull
    @Override
    public String getAuthor() {
        return author;
    }

    @NotNull
    @Override
    public String getVersion() {
        return moduleVersion;
    }

    @NotNull
    @Override
    public String getMinimumDiscord4JVersion() {
        return moduleMinimumVersion;
    }
}
