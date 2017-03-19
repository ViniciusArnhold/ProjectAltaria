package me.viniciusarnhold.altaria.events;

import me.viniciusarnhold.altaria.events.handler.BotInfoCommandHandler;
import me.viniciusarnhold.altaria.events.handler.GoogleCommandHandler;
import me.viniciusarnhold.altaria.events.handler.SimpleCommandHandler;
import me.viniciusarnhold.altaria.events.interfaces.ICommandHandler;
import me.viniciusarnhold.altaria.events.utils.Commands;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.EntryMessage;
import org.jetbrains.annotations.NotNull;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;

import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Created by Vinicius.
 *
 * @since ${PROJECT_VERSION}
 */
public final class CommandDelegator {

    public static final Logger logger = LogManager.getLogger();

    private static final ConcurrentSkipListSet<Commands> COMMANDS = new ConcurrentSkipListSet<>();

    private static final ConcurrentSkipListSet<ICommandHandler> HANDLERS = new ConcurrentSkipListSet<>(Comparator.comparing(o -> o.getClass().getName()));
    @NotNull
    private static CommandDelegator ourInstance = new CommandDelegator();

    static {
        //Register HANDLERS
        Collections.addAll(HANDLERS, BotInfoCommandHandler.getInstance(), SimpleCommandHandler.getInstance(), GoogleCommandHandler.getInstance());

        //Register Commands
        HANDLERS.forEach(handler -> COMMANDS.addAll(handler.getHandableCommands()));
    }

    private CommandDelegator() {
    }

    @NotNull
    public static CommandDelegator getInstance() {
        return ourInstance;
    }

    public static final Set<Commands> getAllCommands() {
        return Collections.unmodifiableSet(COMMANDS);
    }

    public final boolean degelateCommand(MessageReceivedEvent event, String command, String matchedText) {
        EntryMessage entryMessage = logger.traceEntry("CommandDelegator will delegate command {}.", matchedText);

        int handleCount = 0;
        for (ICommandHandler handler : HANDLERS) {
            try {
                if (handler.handle(event, command, matchedText)) {
                    handleCount++;
                }
            } catch (Exception e) {
                logger.error("Handler " + handler.getClass().getSimpleName() + " failed to handle command.", e);
            }
        }
        return logger.traceExit(entryMessage, handleCount > 0);
    }

}
