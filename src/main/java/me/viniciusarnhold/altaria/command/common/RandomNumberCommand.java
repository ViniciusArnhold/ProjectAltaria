package me.viniciusarnhold.altaria.command.common;

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
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * Created by Vinicius.
 *
 * @since ${PROJECT_VERSION}
 */
public class RandomNumberCommand extends AbstractCommand {

    private static final Logger logger = LogManager.getLogger();

    // private static final Pattern DICE_PATTERN = Pattern.compile("^d(\\d+)$");
    private static final Pattern INT_PATTERN = Pattern.compile("^\\d{1,9}$");

    private static final Pattern LONG_PATTERN = Pattern.compile("^\\d{1,18}$");

    private static final Set<String> DICE_ROLLS;

    private static final Set<String> COIN_ROLLS;

    static {
        DICE_ROLLS = new ImmutableSet.Builder<String>()
                .add("d2")
                .add("d3")
                .add("d4")
                .add("d6")
                .add("d8")
                .add("d10")
                .add("d12")
                .add("d14")
                .add("d16")
                .add("d18")
                .add("d20")
                .add("d24")
                .add("d30")
                .add("d34")
                .add("d48")
                .add("d50")
                .add("d60")
                .add("d100")
                .add("d120")
                .build();
        COIN_ROLLS = ImmutableSet.of("coin", "coinToss", "tossCoin");
    }

    public RandomNumberCommand() {
        super();
        this.command = "random";
        this.aliases = new ImmutableSet.Builder<String>()
                .add("randomNumber")
                .addAll(DICE_ROLLS)
                .addAll(COIN_ROLLS)
                .build();

        this.commandType = CommandType.GENERAL;
        this.description = "Generates a ranndom number, use dNUM for a shortcut.";
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
        logger.traceEntry("Received uptime command");
        try {

            @NotNull List<String> args = Commands.splitByWhitespace(event.getMessage().getContent().trim());

            @NotNull String firstArg = args.get(0).substring(1);
            if (DICE_ROLLS.contains(firstArg)) {
                handleDiceRoll(event, args);
                return;
            }
            if (COIN_ROLLS.contains(firstArg)) {
                handleCoinToss(event, args);
                return;
            }
            long iterations = 1;
            if (args.size() > 2 && LONG_PATTERN.matcher(args.get(1)).find() && LONG_PATTERN.matcher(args.get(2)).find()) {
                long origin = Long.parseUnsignedLong(args.get(1));
                long bound = Long.parseUnsignedLong(args.get(2));
                if (origin > bound) {
                    long h = origin;
                    origin = bound;
                    bound = h;
                }
                if (args.size() > 3 && INT_PATTERN.matcher(args.get(3)).find()) {
                    iterations = Math.min(12, Integer.parseUnsignedInt(args.get(3)));
                }
                long sIterations = 0;

                ThreadLocalRandom random = ThreadLocalRandom.current();
                EmbedBuilder builder = MessageUtils.getEmbedBuilder(event.getMessage().getAuthor());
                long sum = 0;
                while (iterations-- != 0) {
                    long num = random.nextLong(origin, bound + 1);
                    sum += num;
                    builder.appendField("[" + origin + "," + bound + "]", num + "", true);
                    sIterations++;
                }
                builder.withTitle("Generated " + sIterations + " random " + (sIterations == 1 ? " number" : " numbers"));
                builder.withDescription("Average result: " + sum / sIterations);

                sendThenDelete(MessageUtils.getMessageBuilder(event.getMessage())
                        .withEmbed(builder.build()));
                delete(event.getMessage());
            } else {
                sendThenDelete(MessageUtils.getMessageBuilder(event.getMessage())
                        .withContent("Usage: random origin bound <iterations>"));
            }

        } catch (Exception e) {
            logger.error(e);
        }
    }

    private void handleCoinToss(@NotNull MessageReceivedEvent event, @NotNull List<String> args) {
        int iterations = 1;
        if (args.size() > 1 && INT_PATTERN.matcher(args.get(1)).find()) {
            iterations = Math.min(12, Integer.parseUnsignedInt(args.get(1)));
        }
        int sIterations = 0;
        ThreadLocalRandom random = ThreadLocalRandom.current();
        EmbedBuilder builder = MessageUtils.getEmbedBuilder(event.getMessage().getAuthor());
        int heads = 0;
        int tails = 0;
        while (sIterations++ != iterations) {
            boolean res = random.nextBoolean();

            if (res) heads++;
            else tails++;

            builder.appendField("Round " + (sIterations), res ? "Head" : "Tail", true);
        }
        builder.withTitle("Tossed " + (sIterations - 1) + (sIterations == 1 ? " coin" : " coins"));
        builder.withDescription("Heads: " + heads + ". Tails: " + tails);

        sendThenDelete(MessageUtils.getMessageBuilder(event.getMessage())
                .withEmbed(builder.build()));
        delete(event.getMessage());
    }

    private void handleDiceRoll(@NotNull MessageReceivedEvent event, @NotNull List<String> args) {
        int upper = Integer.parseUnsignedInt(args.get(0).substring(2));
        int iterations = 1;
        if (args.size() > 1 && INT_PATTERN.matcher(args.get(1)).find()) {
            iterations = Math.min(12, Integer.parseUnsignedInt(args.get(1)));
        }
        int sIterations = iterations;
        ThreadLocalRandom random = ThreadLocalRandom.current();
        EmbedBuilder builder = MessageUtils.getEmbedBuilder(event.getMessage().getAuthor());
        int sum = 0;
        while (iterations-- != 0) {
            int num = random.nextInt(1, upper + 1);
            sum += num;
            builder.appendField(":game_die: [1," + upper + "]", num + "", true);
        }
        builder.withTitle("Tossed " + sIterations + (sIterations == 1 ? " dice" : " dices"));
        builder.withDescription("Average result: " + (sum / sIterations));

        sendThenDelete(MessageUtils.getMessageBuilder(event.getMessage())
                .withEmbed(builder.build()));
        delete(event.getMessage());
    }

    private void sendThenDelete(@NotNull MessageBuilder builder) {
        RequestBuffer.request(Actions.wrap(() -> {
            IMessage message = builder.send();
            Timers.messageDeletionService().schedule(message, 1, TimeUnit.HOURS);
        }));
    }

    private void delete(@NotNull IMessage message) {
        RequestBuffer.request(Actions.wrap(message::delete));
    }
}
