package me.viniciusarnhold.altaria.command.common

import com.google.common.collect.ImmutableSet
import me.viniciusarnhold.altaria.command.AbstractMessageCommand
import me.viniciusarnhold.altaria.command.CommandType
import me.viniciusarnhold.altaria.command.MessageUtils
import me.viniciusarnhold.altaria.command.UserPermissions
import me.viniciusarnhold.altaria.events.utils.Commands
import me.viniciusarnhold.altaria.utils.Actions
import me.viniciusarnhold.altaria.utils.Timers
import org.apache.logging.log4j.LogManager
import sx.blah.discord.handle.impl.events.MessageReceivedEvent
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.util.MessageBuilder
import sx.blah.discord.util.RequestBuffer
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

/**
 * Created by Vinicius.

 * @since ${PROJECT_VERSION}
 */
class RandomNumberCommand : AbstractMessageCommand() {
    init {
        this.command = "random"
        this.aliases = ImmutableSet.Builder<String>()
                .add("randomNumber")
                .addAll(DICE_ROLLS)
                .addAll(COIN_ROLLS)
                .build()

        this.commandType = me.viniciusarnhold.altaria.command.CommandType.GENERAL
        this.description = "Generates a ranndom number, use dNUM for a shortcut."
        this.permissions = EnumSet.noneOf<UserPermissions>(UserPermissions::class.java)
    }

    /**
     * Called when the event is sent.

     * @param event The event object.
     */
    override fun handle(event: MessageReceivedEvent) {
        if (!isCommandForMe(event)) {
            return
        }
        logger.traceEntry("Received uptime command {}", event)
        try {

            val args = Commands.splitByWhitespace(event.message.content.trim { it <= ' ' })

            val firstArg = args[0].substring(1)
            if (DICE_ROLLS.contains(firstArg)) {
                handleDiceRoll(event, args)
                return
            }
            if (COIN_ROLLS.contains(firstArg)) {
                handleCoinToss(event, args)
                return
            }
            var iterations: Long = 1
            if (args.size > 2 && LONG_PATTERN.matcher(args[1]).find() && LONG_PATTERN.matcher(args[2]).find()) {
                var origin = java.lang.Long.parseUnsignedLong(args[1])
                var bound = java.lang.Long.parseUnsignedLong(args[2])
                if (origin > bound) {
                    val h = origin
                    origin = bound
                    bound = h
                }
                if (args.size > 3 && INT_PATTERN.matcher(args[3]).find()) {
                    iterations = Math.min(12, Integer.parseUnsignedInt(args[3])).toLong()
                }
                var sIterations: Long = 0

                val random = ThreadLocalRandom.current()
                val builder = MessageUtils.getEmbedBuilder(event.message.author)
                var sum: Long = 0
                while (iterations-- != 0L) {
                    val num = random.nextLong(origin, bound + 1)
                    sum += num
                    builder.appendField("[$origin,$bound]", num.toString() + "", true)
                    sIterations++
                }
                with(builder) {
                    withTitle("Generated " + sIterations + " random " + if (sIterations == 1L) " number" else " numbers")
                    withDescription("Average result: " + sum / sIterations)
                }

                sendThenDelete(MessageUtils.getMessageBuilder(event.message)
                        .withEmbed(builder.build()))
                delete(event.message)
            } else {
                sendThenDelete(MessageUtils.getMessageBuilder(event.message)
                        .withContent("Usage: random origin bound <iterations>"))
            }

        } catch (e: Exception) {
            logger.error(e)
        }

    }

    private fun handleCoinToss(event: MessageReceivedEvent, args: List<String>) {
        var iterations = 1
        if (args.size > 1 && INT_PATTERN.matcher(args[1]).find()) {
            iterations = Math.min(12, Integer.parseUnsignedInt(args[1]))
        }
        var sIterations = 0
        val random = ThreadLocalRandom.current()
        val builder = MessageUtils.getEmbedBuilder(event.message.author)
        var heads = 0
        var tails = 0
        while (sIterations++ != iterations) {
            val res = random.nextBoolean()

            if (res)
                heads++
            else
                tails++

            builder.appendField("Round " + sIterations, if (res) "Head" else "Tail", true)
        }
        builder.withTitle("Tossed " + (sIterations - 1) + if (sIterations == 1) " coin" else " coins")
        builder.withDescription("Heads: $heads. Tails: $tails")

        sendThenDelete(MessageUtils.getMessageBuilder(event.message)
                .withEmbed(builder.build()))
        delete(event.message)
    }

    private fun handleDiceRoll(event: MessageReceivedEvent, args: List<String>) {
        val upper = Integer.parseUnsignedInt(args[0].substring(2))
        var iterations = 1
        if (args.size > 1 && INT_PATTERN.matcher(args[1]).find()) {
            iterations = Math.min(12, Integer.parseUnsignedInt(args[1]))
        }
        val sIterations = iterations
        val random = ThreadLocalRandom.current()
        val builder = MessageUtils.getEmbedBuilder(event.message.author)
        var sum = 0
        while (iterations-- != 0) {
            val num = random.nextInt(1, upper + 1)
            sum += num
            builder.appendField(":game_die: [1,$upper]", num.toString() + "", true)
        }
        builder.withTitle("Tossed " + sIterations + if (sIterations == 1) " dice" else " dices")
        builder.withDescription("Average result: " + sum / sIterations)

        sendThenDelete(MessageUtils.getMessageBuilder(event.message)
                .withEmbed(builder.build()))
        delete(event.message)
    }

    private fun sendThenDelete(builder: MessageBuilder) {
        RequestBuffer.request(Actions.wrap {
            val message = builder.send()
            Timers.MessageDeletionService.schedule(message, 1, TimeUnit.HOURS)
        })
    }

    private fun delete(message: IMessage) {
        RequestBuffer.request(Actions.wrap({ message.delete() }))
    }

    companion object {

        private val logger = LogManager.getLogger()

        // private static final Pattern DICE_PATTERN = Pattern.compile("^d(\\d+)$");
        private val INT_PATTERN = Pattern.compile("^\\d{1,9}$")

        private val LONG_PATTERN = Pattern.compile("^\\d{1,18}$")

        private val DICE_ROLLS: Set<String>

        private val COIN_ROLLS: Set<String>

        init {
            DICE_ROLLS = ImmutableSet.Builder<String>()
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
                    .build()
            COIN_ROLLS = ImmutableSet.of("coin", "coinToss", "tossCoin")
        }
    }
}
