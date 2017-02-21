package me.viniciusarnhold.altaria.events;

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

import java.util.regex.Matcher;

/**
 * Created by Vinicius.
 *
 * @since ${PROJECT_VERSION}
 */
public class SingleArgsEvent implements IDiscordEvent, IListener<MessageReceivedEvent> {

    private static final Logger logger = LogManager.getLogger();

    private static final String MAIN_COMMAND = EventManager.MAIN_COMMAND_NAME.toLowerCase();

    private static SingleArgsEvent ourInstance = new SingleArgsEvent();

    static {
        EventManager.registerCommand("Hello \"to\"", "Send a 'Hello 'to'' message.", RegexEvents.SIMPLE_ARGS);
    }

    public SingleArgsEvent() {
    }

    public static SingleArgsEvent getInstance() {
        return ourInstance;
    }

    private void sendHelloWorld(MessageReceivedEvent event, String to) throws DiscordException, MissingPermissionsException, RateLimitException {
        new MessageBuilder(event.getClient())
                .appendContent("Hello ")
                .appendContent(to.trim())
                .withChannel(event.getMessage().getChannel())
                .send();
    }

    @Override
    public void handle(MessageReceivedEvent event) {
        IMessage message = event.getMessage();
        Matcher matcher = RegexEvents.SIMPLE_ARGS.getRegex().matcher(message.getContent().trim());

        if (matcher.find()) {
            EventUtils.logMessageEventReceived(event, this.getClass());
            try {
                switch (matcher.group(1).toLowerCase()) {
                    case "hello":
                        sendHelloWorld(event, matcher.group(2));
                        break;
                    default:
                        //NoMessage
                        break;
                }
            } catch (Exception e) {
                logger.error("Failed during " + this.getClass().getSimpleName() + " event handling.", e);
            }
        }
    }

    @Override
    public void terminate() {

    }
}
