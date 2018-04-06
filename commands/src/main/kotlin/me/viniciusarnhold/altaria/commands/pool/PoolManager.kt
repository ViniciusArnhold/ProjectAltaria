package me.viniciusarnhold.altaria.commands.pool

import com.vdurmont.emoji.EmojiManager
import me.viniciusarnhold.altaria.api.MessageUtils
import me.viniciusarnhold.altaria.utils.Timers
import org.apache.logging.log4j.LogManager
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.handle.obj.IUser
import sx.blah.discord.util.DiscordException
import sx.blah.discord.util.MissingPermissionsException
import sx.blah.discord.util.RateLimitException
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by Vinicius.

 * @since 1.0
 */
class PoolManager private constructor() {

    fun registerPool(command: PoolCommand, message: IMessage, pool: Pool) {
        logger.trace("Registering new pool {}, called by {} for {} seconds.",
                { pool.id() },
                { message.author.name },
                { TimeUnit.MILLISECONDS.convert(pool.time(), TimeUnit.SECONDS) })

        Timer("Pool timer" + pool.id(), true)
                .schedule(PoolFinalizerTask(command, pool), TimeUnit.MILLISECONDS.convert(pool.time(), TimeUnit.SECONDS))
    }


    private class PoolFinalizerTask(private val command: PoolCommand, private val pool: Pool) : TimerTask() {

        /**
         * The action to be performed by this timer task.
         */
        override fun run() {
            val channel = this.pool.message().channel
            val currentMessage = channel.getMessageByID(this.pool.message().longID)
            if (currentMessage == null) {
                logger.trace("Pool {} finished but the message was Deleted", pool.id())
                return
            }

            val options = pool.options()
            val results = HashMap<String, List<IUser>>()


            if (pool.type() == Pool.Type.MULTI) {
                for ((key) in options) {
                    val reaction = currentMessage.getReactionByUnicode(EmojiManager.getForAlias(key).unicode)
                    if (reaction != null) {
                        try {
                            results.put(key, reaction.users.filter { !it.isBot })
                        } catch (e: RateLimitException) {
                            logger.error(e)
                            return
                        } catch (e: DiscordException) {
                            logger.error(e)
                            return
                        }

                    }
                }
            } else {
                val users = HashSet<IUser>()
                for ((key) in options) {
                    val reaction = currentMessage.getReactionByUnicode(EmojiManager.getForAlias(key).unicode)
                    if (reaction != null) {
                        val rUsers = try {
                            reaction.users.filter { !it.isBot }.toMutableList()
                        } catch (e: RateLimitException) {
                            logger.error(e)
                            mutableListOf<IUser>()
                        } catch (e: DiscordException) {
                            logger.error(e)
                            mutableListOf<IUser>()
                        }

                        rUsers.removeAll(users)
                        results.put(key, rUsers)
                        users.addAll(rUsers)
                    }
                }
            }
            val builder = MessageUtils.getEmbedBuilder(currentMessage.author)
                    .withTitle("Pool Results")

            results.entries.stream()
                    .sorted { o1, o2 -> o2.value.size - o1.value.size }
                    .filter { e -> e.value.size > 0 }
                    .forEachOrdered { e ->
                        builder.appendField(
                                e.key + " - " + options[e.key],
                                e.value.asSequence()
                                        .map(transform = { it.name })
                                        .take(n = 15)
                                        .joinToString(separator = ", "),
                                false)
                    }

            val winner = results.entries.maxWith(comparator = kotlin.Comparator.comparingInt { e -> e.value.size })?.run({
                "$key - ${options[key]}"
            }) ?: "No Votes"

            builder.withDescription("Winner: $winner")

            try {
                Timers.MessageDeletionService.schedule(
                        MessageUtils.getMessageBuilder(currentMessage.channel)
                                .withEmbed(builder.build())
                                .send())

                pool.message().delete()

            } catch (e: RateLimitException) {
                MessageUtils.handleDiscord4JException(logger, e, command, currentMessage)
            } catch (e: MissingPermissionsException) {
                MessageUtils.handleDiscord4JException(logger, e, command, currentMessage)
            } catch (e: DiscordException) {
                MessageUtils.handleDiscord4JException(logger, e, command, currentMessage)
            }

        }

        companion object {

            private val logger = LogManager.getLogger()
        }
    }

    companion object {

        private val logger = LogManager.getLogger()

        val instance = PoolManager()
    }
}
