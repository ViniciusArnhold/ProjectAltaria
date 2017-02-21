package me.viniciusarnhold.altaria.events;

import me.viniciusarnhold.altaria.events.thirdparty.GoogleSearchEvent;
import me.viniciusarnhold.altaria.events.utils.Command;
import me.viniciusarnhold.altaria.events.utils.CommandBuilder;
import me.viniciusarnhold.altaria.events.utils.RegexEvents;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.Event;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.modules.IModule;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Created by Vinicius.
 *
 * @since ${PROJECT_VERSION}
 */
public class EventManager implements IModule {

    public static final String MAIN_COMMAND_NAME = "!alt";

    private static final ConcurrentSkipListSet<Command> COMMANDS = new ConcurrentSkipListSet<>();

    private static final Logger logger = LogManager.getLogger();
    private static IDiscordClient discordClient;
    private static EventManager ourInstance = new EventManager();
    private static Set<IDiscordEvent> eventListeners = new HashSet<>();
    private String moduleName = "Project Altaria";
    private String moduleVersion = "1.0";
    private String moduleMinimumVersion = "2.6";
    private String author = "Vinicius Pegorini Arnhold";

    public EventManager() {
    }

    public static EventManager getInstance() {
        return ourInstance;
    }

    public static final boolean registerCommand(String command, String help, RegexEvents regex) {
        return COMMANDS.add(
                new CommandBuilder()
                        .command(command)
                        .helpText(help)
                        .regex(regex)
                        .create()
        );
    }

    public static Set<Command> getCommands() {
        return COMMANDS;
    }

    public static IDiscordClient getDiscordClient() {
        return discordClient;
    }

    @Override
    public boolean enable(IDiscordClient client) {
        discordClient = client;

        //Simple Events
        registerListener(SingleArgsEvent.getInstance());
        registerListener(new NoArgsEvent());

        //Third Party APIs Event Handlers
        registerListener(GoogleSearchEvent.getInstance());
        //Complex Events


        return true;
    }

    private <T extends IDiscordEvent & IListener<? extends Event>> void registerListener(T eventListener) {
        discordClient.getDispatcher().registerListener(eventListener);
        eventListeners.add(eventListener);
    }

    @Override
    public void disable() {
        for (IDiscordEvent listener :
                eventListeners) {
            listener.terminate();
        }
    }

    @Override
    public String getName() {
        return this.moduleName;
    }

    @Override
    public String getAuthor() {
        return this.author;
    }

    @Override
    public String getVersion() {
        return this.moduleVersion;
    }

    @Override
    public String getMinimumDiscord4JVersion() {
        return this.moduleMinimumVersion;
    }
}
