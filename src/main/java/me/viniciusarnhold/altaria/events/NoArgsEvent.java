package me.viniciusarnhold.altaria.events;

import me.viniciusarnhold.altaria.events.utils.Command;
import me.viniciusarnhold.altaria.events.utils.EventUtils;
import me.viniciusarnhold.altaria.events.utils.RegexEvents;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

import java.text.MessageFormat;
import java.util.Set;
import java.util.regex.Matcher;

/**
 * Created by Vinicius.
 *
 * @since ${PROJECT_VERSION}
 */
public class NoArgsEvent implements IDiscordEvent, IListener<MessageReceivedEvent> {

    private static final Logger logger = LogManager.getLogger();

    static {
        EventManager.registerCommand("Help", "Show this list of commands.", RegexEvents.NO_ARGS);
        EventManager.registerCommand("Info", "Show some info about this bot.", RegexEvents.NO_ARGS);
    }


    /**
     * Called when the event is sent.
     *
     * @param event The event object.
     */
    @Override
    public void handle(MessageReceivedEvent event) {
        IMessage message = event.getMessage();
        Matcher matcher = RegexEvents.NO_ARGS.getRegex().matcher(message.getContent().trim());

        if (matcher.find()) {
            EventUtils.logMessageEventReceived(event, this.getClass());
            try {
                switch (matcher.group(1).toLowerCase()) {
                    case "help":
                        showHelp(event);
                        break;
                    case "info":
                        showInfo(event);
                    default:
                        //NoMessage
                        break;
                }
            } catch (Exception e) {
                logger.error("Failed during " + this.getClass().getSimpleName() + " event handling.", e);
            }
        }
    }

    private void showInfo(MessageReceivedEvent event) throws RateLimitException, DiscordException, MissingPermissionsException {
        StringBuilder builder = new StringBuilder(150);
        EventManager manager = EventManager.getInstance();
        builder.append(manager.getName())
                .append(" - Version: ")
                .append(manager.getVersion())
                .append("\n")
                .append("Created by: ")
                .append(manager.getAuthor())
                .append("\n")
                .append("   Have fun!");

        new MessageBuilder(event.getClient())
                .appendQuote(builder.toString())
                .withChannel(event.getMessage().getChannel())
                .send();
    }

    private void showHelp(MessageReceivedEvent event) throws RateLimitException, DiscordException, MissingPermissionsException {
        Set<Command> commands = EventManager.getCommands();
        StringBuilder builder = new StringBuilder(commands.size() * 75);
        builder.append(MessageFormat.format("List of commands this bot accepts, all commands must start with {0}", EventManager.MAIN_COMMAND_NAME));
        for (Command cmd :
                commands) {
            builder.append("\n")
                    .append(cmd.getCommand())
                    .append(" - ")
                    .append(cmd.getHelpText());
        }
        new MessageBuilder(event.getClient())
                .appendQuote(builder.toString())
                .withChannel(event.getMessage().getChannel())
                .send();

    }


    @Override
    public void terminate() {

    }
}

