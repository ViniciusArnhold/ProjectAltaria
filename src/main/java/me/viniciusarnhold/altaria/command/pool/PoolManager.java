package me.viniciusarnhold.altaria.command.pool;

import com.vdurmont.emoji.EmojiManager;
import me.viniciusarnhold.altaria.command.MessageUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IReaction;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static me.viniciusarnhold.altaria.utils.Timers.messageDeletionService;

/**
 * Created by Vinicius.
 *
 * @since ${PROJECT_VERSION}
 */
public class PoolManager {

    private static final Logger logger = LogManager.getLogger();

    private static final PoolManager ourInstance = new PoolManager();

    private PoolManager() {

    }

    @NotNull
    public static PoolManager getInstance() {
        return ourInstance;
    }

    public final void registerPool(@NotNull IMessage message, @NotNull Pool pool) {
        logger.trace("Registering new pool {}, called by {} for {} seconds.",
                () -> pool.id(),
                () -> message.getAuthor().getName(),
                () -> TimeUnit.MILLISECONDS.convert(pool.time(), TimeUnit.SECONDS));

        new Timer("Pool timer" + pool.id(), true)
                .schedule(new PoolFinalizerTask(pool), TimeUnit.MILLISECONDS.convert(pool.time(), TimeUnit.SECONDS));
    }


    private static class PoolFinalizerTask extends TimerTask {

        private static final Logger logger = LogManager.getLogger();

        private final Pool pool;

        public PoolFinalizerTask(Pool pool) {
            this.pool = pool;
        }

        /**
         * The action to be performed by this timer task.
         */
        @Override
        public void run() {
            IChannel channel = this.pool.message().getChannel();
            IMessage currentMessage = channel.getMessageByID(this.pool.message().getID());
            if (currentMessage == null) {
                logger.trace("Pool {} finished but the message was Deleted", pool.id());
                return;
            }

            final Map<String, String> options = pool.options();
            final Map<String, List<IUser>> results = new HashMap<>();


            if (pool.type() == Pool.Type.MULTI) {
                for (Map.Entry<String, String> entry : options.entrySet()) {
                    IReaction reaction = currentMessage.getReactionByName(EmojiManager.getForAlias(entry.getKey()).getUnicode());
                    if (reaction != null) {
                        try {
                            results.put(entry.getKey(), reaction.getUsers().stream().filter(u -> !u.isBot()).collect(Collectors.toList()));
                        } catch (@NotNull RateLimitException | DiscordException e) {
                            logger.error(e);
                            return;
                        }
                    }
                }
            } else {
                Set<IUser> users = new HashSet<>();
                for (Map.Entry<String, String> entry : options.entrySet()) {
                    IReaction reaction = currentMessage.getReactionByName(EmojiManager.getForAlias(entry.getKey()).getUnicode());
                    if (reaction != null) {
                        List<IUser> rUsers = Collections.emptyList();
                        try {
                            rUsers = reaction.getUsers().stream().filter(u -> !u.isBot()).collect(Collectors.toList());
                        } catch (@NotNull RateLimitException | DiscordException e) {
                            logger.error(e);
                        }
                        rUsers.removeAll(users);
                        results.put(entry.getKey(), rUsers);
                        users.addAll(rUsers);
                    }
                }
            }
            final EmbedBuilder builder = MessageUtils.getEmbedBuilder(currentMessage.getAuthor())
                    .withTitle("Pool Results");

            results.entrySet().stream()
                    .sorted((o1, o2) -> o2.getValue().size() - o1.getValue().size())
                    .filter((e) -> e.getValue().size() > 0)
                    .forEachOrdered(e ->
                            builder.appendField(
                                    e.getKey() + " - " + options.get(e.getKey()),
                                    e.getValue().stream()
                                            .map(IUser::getName)
                                            .limit(15) //TODO Check if 15 is enough or if a embed message can fit more than 15 icons per line
                                            .collect(Collectors.joining(", ")),
                                    false));

            builder.withDescription("Winner: " + results.entrySet().stream()
                    .max(Comparator.comparingInt(e -> e.getValue().size()))
                    .map(e -> e.getKey() + " - " + options.get(e.getKey()))
                    .orElse("No Votes"));

            try {
                messageDeletionService().schedule(
                        MessageUtils.getMessageBuilder(currentMessage.getChannel())
                                .withEmbed(builder.build())
                                .send());

                pool.message().delete();

            } catch (@NotNull RateLimitException | MissingPermissionsException | DiscordException e) {
                MessageUtils.handleDiscord4JException(logger, e, PoolCommand.getInstance(), currentMessage);
            }
        }
    }
}
