package me.viniciusarnhold.altaria.command.pool;

import com.google.common.collect.ImmutableSet;
import com.vdurmont.emoji.EmojiManager;
import me.viniciusarnhold.altaria.command.CommandType;
import me.viniciusarnhold.altaria.command.MessageUtils;
import me.viniciusarnhold.altaria.command.Prefixes;
import me.viniciusarnhold.altaria.command.UserPermissions;
import me.viniciusarnhold.altaria.command.interfaces.ICommand;
import me.viniciusarnhold.altaria.events.utils.Commands;
import me.viniciusarnhold.altaria.utils.Actions;
import me.viniciusarnhold.altaria.utils.TimeUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static me.viniciusarnhold.altaria.utils.Timers.messageDeletionService;


/**
 * Created by Vinicius.
 *
 * @since ${PROJECT_VERSION}
 */
public class PoolCommand implements ICommand, IListener<MessageReceivedEvent> {

    private static final Logger logger = LogManager.getLogger();

    private static final PoolCommand instance = new PoolCommand();

    private static final String POOL_LIST_FORMAT = "%1s - %2s";

    private static final String command = "Pool";

    private static final Set<String> alias = ImmutableSet.of("MultPool", "SinglePool", "MPool");

    private static final String desc = "Creates a timed pool with the given choices, then posts back the results";

    private static final CommandType type = CommandType.UTIL;

    private static final EnumSet<UserPermissions> permissions = EnumSet.of(UserPermissions.MANAGE_POOL);

    private static final List<String> LIST_EMOJIS = Arrays.asList(
            ":one:",
            ":two:",
            ":three:",
            ":four:",
            ":five:",
            ":six:",
            ":seven:",
            ":eight:",
            ":nine:",
            ":keycap_ten:",
            ":regional_indicator_a:",
            ":regional_indicator_b:",
            ":regional_indicator_c:",
            ":regional_indicator_d:",
            ":regional_indicator_e:",
            ":regional_indicator_f:",
            ":regional_indicator_g:",
            ":regional_indicator_h:",
            ":regional_indicator_i:",
            ":regional_indicator_j:",
            ":regional_indicator_k:",
            ":regional_indicator_j:",
            ":regional_indicator_m:",
            ":regional_indicator_n:",
            ":regional_indicator_o:",
            ":regional_indicator_p:",
            ":regional_indicator_q:",
            ":regional_indicator_r:",
            ":regional_indicator_s:",
            ":regional_indicator_t:",
            ":regional_indicator_u:",
            ":regional_indicator_v:",
            ":regional_indicator_w:",
            ":regional_indicator_x:",
            ":regional_indicator_y:",
            ":regional_indicator_z:");

    public PoolCommand() {
        logger.traceEntry();
        logger.traceExit();
    }

    @NotNull
    public static PoolCommand getInstance() {
        return instance;
    }

    private boolean isPoolMessage(@NotNull IMessage message) {
        return !message.getChannel().isPrivate() && MessageUtils.isMyCommand(message, this);
    }

    @NotNull
    @Override
    public String command() {
        return command;
    }

    @NotNull
    @Override
    public Set<String> aliases() {
        return alias;
    }

    @NotNull
    @Override
    public String description() {
        return desc;
    }

    @NotNull
    @Override
    public CommandType type() {
        return type;
    }

    @NotNull
    @Override
    public EnumSet<UserPermissions> permissions() {
        return permissions;
    }

    /**
     * Called when the event is sent.
     *
     * @param event The event object.
     */
    @Override
    public void handle(@NotNull MessageReceivedEvent event) {
        IMessage message = event.getMessage();
        logger.traceEntry("Received command by {} with message {}", message::getAuthor, message::getContent);

        try {
            if (!isPoolMessage(message)) return;

            @NotNull List<String> args = Commands.splitByWhitespace(message.getContent().trim());
            if (args.size() < 4 || args.size() > 12) {
                messageDeletionService().schedule(MessageUtils.getSimpleMentionMessage(message)
                        .appendContent(args.size() > 37 ? "Too many options, max: 12 " : "")
                        .appendContent("Usage: ")
                        .appendContent(Prefixes.getInstance().current())
                        .appendContent(command)
                        .appendContent("\"time\" \"Option1\" \"Option2\" ...")
                        .appendContent(System.lineSeparator())
                        .appendContent("See help for more examples.")
                        .send());

                return;
            }

            Integer time;
            try {
                time = Integer.parseInt(args.get(1));
            } catch (@NotNull final NumberFormatException nfe) {
                logger.trace("Received Pool command but couldnt parse time argument, arg {} ex {}", () -> args.get(1), () -> ExceptionUtils.getStackTrace(nfe));
                messageDeletionService().schedule(MessageUtils.getSimpleMentionMessage(message)
                        .withContent("Second argument must be a number.")
                        .send());
                return;
            }

            @NotNull final Map<String, String> options = new LinkedHashMap<>();
            int count = 0;
            for (int i = 2; i < args.size(); i++) {
                options.put(LIST_EMOJIS.get(count++), args.get(i));
            }

            EmbedBuilder builder = MessageUtils.getEmbedBuilder(message.getAuthor())
                    .withTitle("Pool Created")
                    .withDescription("This pool will last " + TimeUtils.formatToString(time, TimeUnit.SECONDS)
                            + System.lineSeparator()
                            + "React to this message with the given Emojis to vote");

            for (@NotNull Map.Entry<String, String> entry : options.entrySet()) {
                builder.appendField(entry.getValue(), String.format(POOL_LIST_FORMAT, entry.getKey(), entry.getValue()), false);
            }

            final IMessage poolMessage = MessageUtils.getMessageBuilder(message)
                    .withEmbed(builder.build())
                    .withTTS()
                    .send();

            final RequestBuilder requestBuilder = MessageUtils.getDefaultRequestBuilder(message)
                    .doAction(Actions.ofSuccess());

            for (String emoji : options.keySet()) {
                requestBuilder.andThen(Actions.ofSuccess(
                        () -> poolMessage.addReaction(
                                EmojiManager.getForAlias(emoji)
                                        .getUnicode()))
                );
            }
            requestBuilder.andThen(Actions.ofSuccess(message::delete));

            PoolManager.getInstance()
                    .registerPool(message,
                            new Pool(options,
                                    poolMessage,
                                    time,
                                    "MultPool".equalsIgnoreCase(args.get(0))
                                            || "MPool".equalsIgnoreCase(args.get(0)) ?
                                            Pool.Type.MULTI :
                                            Pool.Type.SINGLE));

            requestBuilder.execute();

        } catch (@NotNull RateLimitException | MissingPermissionsException | DiscordException e) {
            MessageUtils.handleDiscord4JException(logger, e, this, message);
        }
    }
}
