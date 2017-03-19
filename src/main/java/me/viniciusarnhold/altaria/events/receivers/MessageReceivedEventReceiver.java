package me.viniciusarnhold.altaria.events.receivers;

import me.viniciusarnhold.altaria.events.CommandDelegator;
import me.viniciusarnhold.altaria.events.interfaces.IReceiver;
import me.viniciusarnhold.altaria.events.utils.Regexes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import sx.blah.discord.api.events.Event;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;

import java.util.regex.Matcher;

/**
 * Created by Vinicius.
 *
 * @since ${PROJECT_VERSION}
 */
public class MessageReceivedEventReceiver implements IReceiver, IListener<MessageReceivedEvent> {

    private static final Logger logger = LogManager.getLogger();

    private static final String CLASS_NAME = MessageReceivedEventReceiver.class.getSimpleName();

    private static final MessageReceivedEventReceiver ourInstance = new MessageReceivedEventReceiver();

    private MessageReceivedEventReceiver() {
    }

    @NotNull
    public static final MessageReceivedEventReceiver getInstace() {
        return ourInstance;
    }

    public static void logMessageEventReceived(MessageReceivedEvent event, String text) {
    }

    @NotNull
    @Override
    public Class<? extends Event> getEventType() {
        return MessageReceivedEvent.class;
    }

    @Override
    public void disable() {
        logger.info("Receiver {} was disabled.", getClass().getName());
    }

    /**
     * Called when the event is sent.
     *
     * @param event The event object.
     */
    @Override
    public void handle(@NotNull final MessageReceivedEvent event) {
        logger.traceEntry("MessageReceivedEventReceiver will handle {}.", event::getMessage);

        String text = event.getMessage().getContent().trim();

        boolean someoneHandled = false;
        Matcher matcher = Regexes.BOT_COMMAND_NO_ARGS.pattern().matcher(text);

        if (!matcher.find()) {
            logger.traceExit("Message {} received but did not pass the command regex.", text);
            return;
        }
        logger.debug("Event receiver {} received commannd: {}", CLASS_NAME, matcher.group());

        someoneHandled = CommandDelegator.getInstance().degelateCommand(event, matcher.group(1), text);

        logger.traceExit("MessageReceivedEvent was handled: {} times.", someoneHandled);
    }

}

