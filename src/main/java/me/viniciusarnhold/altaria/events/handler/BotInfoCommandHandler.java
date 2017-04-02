package me.viniciusarnhold.altaria.events.handler;


import com.google.common.collect.Lists;
import me.viniciusarnhold.altaria.events.EventManager;
import me.viniciusarnhold.altaria.events.StatsCounter;
import me.viniciusarnhold.altaria.events.interfaces.ICommandHandler;
import me.viniciusarnhold.altaria.events.utils.Commands;
import me.viniciusarnhold.altaria.events.utils.EventUtils;
import me.viniciusarnhold.altaria.utils.TimeUtils;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

import javax.validation.constraints.NotNull;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

/**
 * Created by Vinicius.
 *
 * @since ${PROJECT_VERSION}
 */
public final class BotInfoCommandHandler implements ICommandHandler {

    private static final Logger LOGGER = LogManager.getLogger();

    private static final BotInfoCommandHandler ourInstance = new BotInfoCommandHandler();


    @org.jetbrains.annotations.NotNull
    @NotNull
    private static final DateTimeFormatter DATE_TIME_FORMATTER;

    @org.jetbrains.annotations.NotNull
    @NotNull
    private static final Commands HELP_COMMAND;

    @org.jetbrains.annotations.NotNull
    @NotNull
    private static final Commands INFO_COMMAND;
    private static final List<Commands> commands;

    static {
        DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm:ss");
    }

    static {
        Options options = new Options()
                .addOption("v", "verbose", false, "shows the -help for each command, will send the message by PM because of size");
        HELP_COMMAND = Commands.of("Help", options, ourInstance, "Shows this!");

        options = new Options()
                .addOption("v", "verbose", false, "Shows aditional stats, like uptime etc.");
        INFO_COMMAND = Commands.of("Info", options, ourInstance, "Shows some information about bot.");
        commands = Lists.newArrayList(HELP_COMMAND, INFO_COMMAND);
    }

    private BotInfoCommandHandler() {

    }


    @org.jetbrains.annotations.NotNull
    @NotNull
    public static BotInfoCommandHandler getInstance() {
        return ourInstance;
    }

    private void showInfo(@org.jetbrains.annotations.NotNull @NotNull MessageReceivedEvent event, boolean verbose) throws RateLimitException, DiscordException, MissingPermissionsException {
        @org.jetbrains.annotations.NotNull StringBuilder builder = new StringBuilder(verbose ? 300 : 150);
        @org.jetbrains.annotations.NotNull EventManager manager = EventManager.getInstance();
        builder.append(manager.getName())
                .append(" - Version: ")
                .append(manager.getVersion())
                .append(lineSeparator())
                .append("Created by: ")
                .append(manager.getAuthor())
                .append(lineSeparator());
        if (verbose) {
            builder.append("Uptime: ").append(ManagementFactory.getRuntimeMXBean().getUptime())
                    .append(" (Since: ").append(DATE_TIME_FORMATTER.format(ZonedDateTime.now())).append(')')
                    .append(lineSeparator())
                    .append("Commands Handled: ").append(StatsCounter.commandsHandled())
                    .append(lineSeparator())
                    .append("Commands handle time :")
                    .append(lineSeparator())
                    .append("Max: ").append(TimeUtils.formatToString(StatsCounter.maxCommandHandleTime())).append(' ')
                    .append("Mean: ").append(TimeUtils.formatToString(StatsCounter.meanCommandHandleTime())).append(' ')
                    .append("Min: ").append(TimeUtils.formatToString(StatsCounter.minCommandHandleTime())).append(' ')
                    .append(lineSeparator());
        }
        builder.append("   Have fun!");

        new MessageBuilder(event.getClient())
                .appendQuote(builder.toString())
                .withChannel(event.getMessage().getChannel())
                .send();
    }

    private void showHelp(@org.jetbrains.annotations.NotNull @NotNull MessageReceivedEvent event, boolean verbose) throws RateLimitException, DiscordException, MissingPermissionsException {
        if (verbose) {

            @org.jetbrains.annotations.NotNull StringWriter writer = new StringWriter(1000);
            @org.jetbrains.annotations.NotNull PrintWriter pWriter = new PrintWriter(writer);
            pWriter.append("Heres  everything I can do!");
            pWriter.append(lineSeparator());

            for (@org.jetbrains.annotations.NotNull Commands command : Commands.getAllCommands()) {
                new HelpFormatter().printHelp(
                        pWriter,
                        100,
                        EventManager.MAIN_COMMAND_NAME + ' ' + command.mainCommand(),
                        command.info(),
                        command.options(),
                        5,
                        3,
                        "",
                        true);
                pWriter.append(lineSeparator());
            }
            pWriter.close();

            new MessageBuilder(event.getClient())
                    .appendQuote(writer.toString())
                    .withChannel(event.getMessage().getAuthor().getOrCreatePMChannel())
                    .send();

            new MessageBuilder(event.getClient())
                    .appendContent(event.getMessage().getAuthor().mention())
                    .appendContent("   ")
                    .appendContent("Check your PM`s!")
                    .withChannel(event.getMessage().getChannel())
                    .send();
            return;
        }


        @org.jetbrains.annotations.NotNull Set<Commands> commands = Commands.getAllCommands();
        @org.jetbrains.annotations.NotNull StringWriter writer = new StringWriter(1000);
        PrintWriter pWriter = new PrintWriter(writer)
                .append("List of commands this bot accepts, all commands must start with ")
                .append(EventManager.MAIN_COMMAND_NAME)
                .append(lineSeparator())
                .append("Commands names are case-insensitive.")
                .append(lineSeparator());

        for (@org.jetbrains.annotations.NotNull Commands command :
                commands) {
            new HelpFormatter().printUsage(pWriter,
                    100,
                    EventManager.MAIN_COMMAND_NAME + ' ' + command.mainCommand(),
                    command.options());
            pWriter.append("  ")
                    .append(command.info())
                    .append(lineSeparator());
        }
        new MessageBuilder(event.getClient())
                .appendQuote(writer.toString())
                .withChannel(event.getMessage().getChannel())
                .send();

        pWriter.close();
    }

    @Override
    public boolean handle(@org.jetbrains.annotations.NotNull @NotNull MessageReceivedEvent event, String commandName, @org.jetbrains.annotations.NotNull @NotNull String matchedText) throws RateLimitException, DiscordException, MissingPermissionsException {

        if (INFO_COMMAND.mainCommand().equalsIgnoreCase(commandName)) {
            @org.jetbrains.annotations.NotNull DefaultParser parser = new DefaultParser();
            try {
                CommandLine cmd = parser.parse(INFO_COMMAND.options(), Commands.splitByWhitespace(matchedText).toArray(new String[]{}));

                if (INFO_COMMAND.showHelpIfPresent(event.getClient(), event.getMessage().getChannel(), cmd)) {
                    return true;
                }
                showInfo(event, cmd.hasOption("v"));

            } catch (ParseException e) {
                LOGGER.info("Parsing failed", e);
                EventUtils.sendIncorrectUsageMessage(event.getClient(), event.getMessage().getChannel(), INFO_COMMAND.mainCommand(), e.getMessage());
            }
            return true;
        }

        if (HELP_COMMAND.mainCommand().equalsIgnoreCase(commandName)) {
            @org.jetbrains.annotations.NotNull DefaultParser parser = new DefaultParser();
            try {
                CommandLine cmd = parser.parse(HELP_COMMAND.options(), Commands.splitByWhitespace(matchedText).toArray(new String[]{}));

                if (HELP_COMMAND.showHelpIfPresent(event.getClient(), event.getMessage().getChannel(), cmd)) {
                    return true;
                }
                showHelp(event, cmd.hasOption("v"));

            } catch (ParseException e) {
                LOGGER.info("Parsing failed", e);
                EventUtils.sendIncorrectUsageMessage(event.getClient(), event.getMessage().getChannel(), HELP_COMMAND.mainCommand(), e.getMessage());
            }

            return true;
        }
        return false;
    }

    @NotNull
    @Override
    public List<Commands> getHandableCommands() {
        return commands;
    }


    private char lineSeparator() {
        return '\n';
    }
}

