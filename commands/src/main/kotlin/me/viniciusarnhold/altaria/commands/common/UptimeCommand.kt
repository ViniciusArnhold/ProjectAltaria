package me.viniciusarnhold.altaria.commands.common

import me.viniciusarnhold.altaria.api.CommandType
import me.viniciusarnhold.altaria.api.MessageCommandContext
import me.viniciusarnhold.altaria.utils.TimeUtils
import java.util.*

/**
 * Created by Vinicius.

 * @since 1.0
 */
class UptimeCommand : me.viniciusarnhold.altaria.api.AbstractMessageCommand() {
    override val command = "uptime"
    override val type: EnumSet<CommandType> = EnumSet.of(CommandType.BOT)
    override val description = "Measures the time this bot has been onnline"

    override fun onCommand(ctx: MessageCommandContext) {
        if (ctx.isPrivate() || ctx.isFromBot()) return

        ctx.reply(ctx.withEmbed()
                .withTitle("Uptime diagnosis")
                .withDescription(TimeUtils.formatElapsed()))

        ctx.enqueue { ctx.message.delete() }
    }
}
