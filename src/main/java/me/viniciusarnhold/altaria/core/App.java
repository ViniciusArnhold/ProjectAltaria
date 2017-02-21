package me.viniciusarnhold.altaria.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sx.blah.discord.util.DiscordException;

/**
 * Main class.
 *
 * @since 1.0
 */
public class App {

    public static void main(String[] args) throws DiscordException {

        //App configuration
        final Logger logger = LogManager.getLogger();

        logger.traceEntry();

        BotManager.getInstance().start();

        logger.traceExit();
    }
}
