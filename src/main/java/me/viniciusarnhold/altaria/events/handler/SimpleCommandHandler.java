package me.viniciusarnhold.altaria.events.handler;

import me.viniciusarnhold.altaria.events.interfaces.ICommandHandler;
import me.viniciusarnhold.altaria.events.utils.Commands;
import me.viniciusarnhold.altaria.events.utils.EventUtils;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Vinicius.
 *
 * @since ${PROJECT_VERSION}
 */
public class SimpleCommandHandler implements ICommandHandler {

    private static final Logger logger = LogManager.getLogger();
    @NotNull
    private static final Commands HELLO_COMMAND;
    @NotNull
    private static SimpleCommandHandler ourInstance = new SimpleCommandHandler();

    static {
        Options options = new Options();
        options.addOption(Option.builder("m")
                .longOpt("mention")
                .type(String.class)
                .desc("When this argument is passed the message becomes '@<args or caller> Hello!'")
                .optionalArg(true)
                .build());
        HELLO_COMMAND = Commands.of("Hello", options, getInstance(), "Answers with `Hello `args`!` or @Person Hello!");
    }

    private SimpleCommandHandler() {
    }

    @NotNull
    public static SimpleCommandHandler getInstance() {
        return ourInstance;
    }

    private void sendHelloWorld(@NotNull MessageReceivedEvent event, boolean isMention, @Nullable String[] to) throws DiscordException, MissingPermissionsException, RateLimitException {
        StringBuilder builder = new StringBuilder(40);
        if (isMention) {
            if (to == null) {
                builder.append(event.getMessage().getAuthor().mention())
                        .append(" ")
                        .append("Hello!");
            } else {
                builder.append("Hello");
                for (String str : to) {
                    builder.append(" ")
                            .append(str);
                }
                builder.append("!");
            }
        } else {
            builder.append("Hello World!");
        }
        new MessageBuilder(event.getClient())
                .withChannel(event.getMessage().getChannel())
                .withContent(builder.toString())
                .send();
    }

    @Override
    public boolean handle(@NotNull MessageReceivedEvent event, String command, @NotNull String matchedText) throws
            RateLimitException, DiscordException, MissingPermissionsException {

        if (HELLO_COMMAND.mainCommand().equalsIgnoreCase(command)) {
            try {
                CommandLine cmd = HELLO_COMMAND.parse(matchedText);

                if (HELLO_COMMAND.showHelpIfPresent(event.getClient(), event.getMessage().getChannel(), cmd)) {
                    return true;
                }

                boolean isMention = cmd.hasOption("m");
                String[] to = cmd.getOptionValues("m");

                sendHelloWorld(event, isMention, to);

            } catch (ParseException e) {
                logger.info("Parsing failed", e);
                EventUtils.sendIncorrectUsageMessage(event.getClient(), event.getMessage().getChannel(), HELLO_COMMAND.mainCommand(), e.getMessage());
            }
            return true;
        }
        return false;
    }

    @NotNull
    @Override
    public List<Commands> getHandableCommands() {
        return Arrays.asList(HELLO_COMMAND);
    }
}
