package me.viniciusarnhold.altaria.commands.common

import me.viniciusarnhold.altaria.api.CommandType
import me.viniciusarnhold.altaria.api.MessageCommandContext
import me.viniciusarnhold.altaria.api.deleteIn
import me.viniciusarnhold.altaria.utils.TimeUtils
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by Vinicius.

 * @since 1.0
 */
class PingCommand : me.viniciusarnhold.altaria.api.AbstractMessageCommand() {

    override val command = "ping"
    override val description = "Measures the time a commands take to reach the bot"
    override val type: EnumSet<CommandType> = EnumSet.of(CommandType.BOT)

    override fun onCommand(ctx: MessageCommandContext) {
        if (ctx.isPrivate() || ctx.isFromBot()) return

        ctx.reply("""
            Pong!
            Last response time in: ${TimeUtils.formatToString(ctx.shard.responseTime, TimeUnit.MILLISECONDS)}
            """.trimIndent())

        ctx.message.deleteIn(1, TimeUnit.HOURS)
    }
}
