package me.viniciusarnhold.altaria.utils;

import me.viniciusarnhold.altaria.core.App;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by Vinicius.
 *
 * @since ${PROJECT_VERSION}
 */
public class Logs {

    private static final Logger logger = LogManager.getLogger(App.class);


    public static void forThrowable(Throwable t) {
        logger.error(t);
    }

    public static void forMessage(Level level, String message) {
        logger.log(level, message);
    }

    public static void forMessage(String message) {
        logger.log(Level.ERROR, message);
    }

}
