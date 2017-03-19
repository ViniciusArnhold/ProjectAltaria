package me.viniciusarnhold.altaria.events.utils;

import me.viniciusarnhold.altaria.events.EventManager;
import me.viniciusarnhold.altaria.events.interfaces.ICommandHandler;
import org.apache.commons.cli.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.regex.Matcher;

/**
 * Created by Vinicius.
 *
 * @since ${PROJECT_VERSION}
 */
public class Commands implements Comparable<Commands> {

    private static final ConcurrentSkipListSet<Commands> commandList = new ConcurrentSkipListSet<>(Commands::compareTo);

    @NotNull
    private final String mainCommand;

    @NotNull
    private final Options options;

    @NotNull
    private final ICommandHandler handler;

    @NotNull
    private final String commandInfo;

    private Commands(@NotNull String mainCommand, @NotNull Options options, @NotNull ICommandHandler handler, @NotNull String commandInfo) {
        this.commandInfo = commandInfo;
        this.mainCommand = mainCommand;
        this.options = options;
        this.handler = handler;

        if (!this.options.hasOption("h")) {
            options.addOption(getDefaultHelpOption());
        } else {
            options.addOption(Option.builder()
                    .desc("prints this option")
                    .longOpt(this.options.hasLongOption("help") ? "showHelp" : "help")
                    .build());
        }
        commandList.add(this);
    }

    @NotNull
    public static Commands of(@NotNull String mainCommand, @NotNull Options options, @NotNull ICommandHandler handler, @NotNull String commandInfo) {
        return new Commands(mainCommand, options, handler, commandInfo);
    }

    public static final SortedSet<Commands> getAllCommands() {
        return Collections.unmodifiableSortedSet(commandList);
    }

    @NotNull
    @Contract(" -> !null")
    public static Option getDefaultHelpOption() {
        return new Option("h", "help", false, "prints this message");
    }

    @NotNull
    @Contract("null -> fail")
    public static List<String> splitByWhitespace(@NotNull String args) {
        Matcher matcher = Regexes.WHITESPACE_SPLIT.pattern().matcher(Objects.requireNonNull(args));

        //As seen on http://stackoverflow.com/a/7804472
        List<String> list = new ArrayList<>();
        while (matcher.find()) {
            list.add(matcher.group(1).replace("\"", ""));
        }
        return list;
    }

    @NotNull
    public String info() {
        return commandInfo;
    }

    @NotNull
    public String mainCommand() {
        return mainCommand;
    }

    @NotNull
    public Options options() {
        return options;
    }

    @NotNull
    public ICommandHandler handler() {
        return handler;
    }

    @Contract("null -> fail")
    public CommandLine parse(@NotNull String text) throws ParseException {
        List<String> splitedText = splitByWhitespace(text);
        return new DefaultParser().parse(this.options, splitedText.toArray(new String[splitedText.size()]));
    }

    public boolean showHelpIfPresent(IDiscordClient client, IChannel channnel, @NotNull CommandLine cmd) throws RateLimitException, DiscordException, MissingPermissionsException {
        if (cmd.hasOption("h") || cmd.hasOption("help") || cmd.hasOption("showHelp")) {
            StringWriter writter = new StringWriter(200);
            PrintWriter pw = new PrintWriter(writter);
            new HelpFormatter()
                    .printHelp(pw, 200,
                            EventManager.MAIN_COMMAND_NAME + "  " + this.mainCommand,
                            this.commandInfo,
                            this.options,
                            3,
                            5,
                            null,
                            true);
            new MessageBuilder(client)
                    .withChannel(channnel)
                    .withQuote(writter.toString())
                    .send();
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Commands command = (Commands) o;
        return Objects.equals(mainCommand, command.mainCommand) &&
                Objects.equals(options, command.options);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mainCommand, options);
    }

    @Override
    public int compareTo(@NotNull Commands o) {
        return (this.mainCommand.compareTo(o.mainCommand) < 0) ? (this.options.equals(o.options) ? 0 : 1) : -1;
    }
}
