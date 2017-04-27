package me.viniciusarnhold.altaria.command.common;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import me.viniciusarnhold.altaria.command.AbstractCommand;
import me.viniciusarnhold.altaria.command.CommandType;
import me.viniciusarnhold.altaria.command.MessageUtils;
import me.viniciusarnhold.altaria.command.UserPermissions;
import me.viniciusarnhold.altaria.events.utils.Commands;
import me.viniciusarnhold.altaria.utils.Actions;
import me.viniciusarnhold.altaria.utils.Timers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.RequestBuffer;

import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Created by Vinicius.
 *
 * @since ${PROJECT_VERSION}
 */
public class EigthBallCommand extends AbstractCommand {

    private static final Logger logger = LogManager.getLogger();

    private static final List<String> ANSWERS;

    static {
        ANSWERS = new ImmutableList.Builder<String>()
                .add("It is certain")
                .add("It is decidedly so")
                .add("Without a doubt")
                .add("Yes definitely")
                .add("You may rely on it")
                .add("As I see it, yes")
                .add("Most likely")
                .add("Outlook good")
                .add("Yes")
                .add("Signs point to yes")
                .add("Reply hazy try again")
                .add("Ask again later")
                .add("Better not tell you now")
                .add("Cannot predict now")
                .add("Concentrate and ask again")
                .add("Don't count on it")
                .add("My sources say no")
                .add("My reply is no")
                .add("Outlook not so good")
                .add("Very doubtful")
                .build();
    }

    public EigthBallCommand() {
        super();
        this.command = "8ball";
        this.aliases = ImmutableSet.of("magicBall", "eightball", "ball8");
        this.commandType = CommandType.GENERAL;
        this.description = "Asks the magic eight ball for its wisdom.";
        this.permissions = EnumSet.noneOf(UserPermissions.class);
    }

    /**
     * Called when the event is sent.
     *
     * @param event The event object.
     */
    @Override
    public void handle(@NotNull MessageReceivedEvent event) {
        if (!isMyCommand(event)) {
            return;
        }
        logger.traceEntry("Received magic ball command {}.", () -> event.getMessage().getContent());

        try {
            @NotNull List<String> args = Commands.splitByWhitespace(event.getMessage().getContent().trim());
            if (args.size() < 2) {
                RequestBuffer.request(Actions.wrap(() -> {
                    IMessage message = MessageUtils.getMessageBuilder(event.getMessage())
                            .appendContent(":8ball: says: ")
                            .appendContent("I gonna need some questions pal.")
                            .send();
                    Timers.messageDeletionService().schedule(message, 1, TimeUnit.HOURS);
                }));
            } else {
                RequestBuffer.request(Actions.wrap(() -> {
                    IMessage message = MessageUtils.getMessageBuilder(event.getMessage())
                            .appendContent(":8ball: says: ")
                            .appendContent(ANSWERS.get(ThreadLocalRandom.current().nextInt(0, ANSWERS.size())))
                            .send();
                    Timers.messageDeletionService().schedule(message, 1, TimeUnit.HOURS);
                }));
            }
            Timers.messageDeletionService().schedule(event.getMessage(), 1, TimeUnit.HOURS);
        } catch (Exception e) {
            logger.error(e);
        }
    }
}
