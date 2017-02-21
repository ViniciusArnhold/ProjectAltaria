package me.viniciusarnhold.altaria.events.utils;

import me.viniciusarnhold.altaria.events.IDiscordEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;

/**
 * Created by Vinicius.
 *
 * @since ${PROJECT_VERSION}
 */
public class EventUtils {

    private EventUtils() {
        throw new Error("No Init");
    }

    public static void logMessageEventReceived(MessageReceivedEvent event, Class<? extends IDiscordEvent> classBeignLogged) {
        Logger logger = LogManager.getLogger(classBeignLogged);
        logger.debug("EventSubscriber {} received commannd: {}", classBeignLogged.getSimpleName(), event.getMessage().getContent());
    }
}
