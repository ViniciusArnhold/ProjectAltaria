package me.viniciusarnhold.altaria.core;

import com.diffplug.common.base.DurianPlugins;
import com.diffplug.common.base.Errors;
import me.viniciusarnhold.altaria.utils.Logs;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sx.blah.discord.util.DiscordException;

import java.net.URISyntaxException;

import static java.lang.ClassLoader.getSystemResource;

/**
 * Main class.
 *
 * @since 1.0
 */
public class App {

    public static void main(String[] args) throws DiscordException {


        System.setProperty("log4j.configurationFile", "log4j2.xml");

        try {
            ((org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false))
                    .setConfigLocation(getSystemResource("log4j2.xml").toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        //App configuration
        final Logger logger = LogManager.getLogger();

        //Config third partys
        DurianPlugins.register(Errors.Plugins.Log.class, Logs::forThrowable);

        logger.traceEntry();

        BotManager.getInstance().start();

        logger.traceExit();
    }
}
