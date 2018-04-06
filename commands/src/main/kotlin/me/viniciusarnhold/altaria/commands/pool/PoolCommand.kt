package me.viniciusarnhold.altaria.commands.pool

import com.vdurmont.emoji.EmojiManager
import me.viniciusarnhold.altaria.api.*
import me.viniciusarnhold.altaria.utils.TimeUtils
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.LinkedHashMap


/**
 * Created by Vinicius.

 * @since 1.0
 */
class PoolCommand : me.viniciusarnhold.altaria.api.AbstractMessageCommand() {
    override val command: String = "pool"
    override val aliases: Set<String> = setOf("multPool", "mPool")
    override val description: String = "Creates a timed pool with the given choices, then posts back the results"
    override val type: EnumSet<CommandType> = EnumSet.of(CommandType.UTIL)
    override val permissions: EnumSet<UserPermissions> = EnumSet.of(UserPermissions.MANAGE_POOL)

    companion object {
        private val EMOJI_LIST = listOf(
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
                ":regional_indicator_z:")
    }

    override fun onCommand(ctx: MessageCommandContext) {
        if (ctx.isPrivate() || ctx.isFromBot()) return

        val args = ctx.command.args
        when (args.size) {
            in 0..2 -> {
                ctx.replyWithError("Need at least time plus two options", true)
                return
            }
            in 13..Int.MAX_VALUE -> {
                ctx.replyWithError("Too many args, max: 12", true)
                return
            }
        }

        val time = args.first().let {
            when (it) {
                is LongArg -> it.longValue
                else -> {
                    ctx.replyWithError("First arg must be the pool time", true)
                    return
                }
            }
        }

        val options = args.drop(1).mapIndexed { index, arg -> Pair(EMOJI_LIST[index], arg.value) }.toMap(LinkedHashMap())

        val embed = ctx.withEmbed()
                .withTitle("Pool created")
                .withDescription("This pool will last ${TimeUtils.formatToString(time, TimeUnit.SECONDS)}")
                .appendDescription(System.lineSeparator())
                .appendDescription("React to this message with the given Emojis to vote")

        options.forEach { key, value -> embed.appendField(value, "${key} - ${value}", false) }

        ctx.withRequest()
                .thenRun {
                    ctx.reply(embed).toCompletable()
                            .thenAcceptAsync { message -> options.keys.forEach { emoji -> ctx.enqueue { message.addReaction(EmojiManager.getForAlias(emoji)) } } }
                }
                .thenRun {
                    ctx.reply(ctx.withMessage("Pool created").withTTS())
                            .toCompletable()
                            .thenAccept { it.deleteIn(5, TimeUnit.MINUTES) }
                }.execute()

        ctx.enqueue { ctx.message.delete() }

        val type = when (ctx.command.name.toUpperCase()) {
            "MPOOL" -> Pool.Type.MULTI
            "MULTPOOL" -> Pool.Type.MULTI
            else -> Pool.Type.SINGLE
        }

        PoolManager.instance.registerPool(this, ctx.message, Pool(options, ctx.message, time, type))
    }
}
