package me.viniciusarnhold.altaria.commands.common

import me.viniciusarnhold.altaria.api.CommandType
import me.viniciusarnhold.altaria.api.MessageCommandContext
import me.viniciusarnhold.altaria.api.deleteIn
import org.apache.logging.log4j.LogManager
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by Vinicius.

 * @since 1.0
 */
class InviteCommand : me.viniciusarnhold.altaria.api.AbstractMessageCommand() {

    override val command = "invite"
    override val aliases = setOf("inv", "invitation")
    override val description = "Returns the invite link for this bot."
    override val type: EnumSet<CommandType> = EnumSet.of(CommandType.BOT)

    override fun onCommand(ctx: MessageCommandContext) {
        if (ctx.isFromBot() || ctx.isPrivate()) return


        val embed = ctx.withEmbed()
                .withTitle("Here's my invite link!")
                .withDescription(ctx.bot.inviteUrl.toString())

        val args = ctx.command.args
        when {
            args.hasParameter("here") -> ctx.reply(embed)
            else -> ctx.replyInPrivate(embed)
        }

        ctx.message.deleteIn(1, TimeUnit.HOURS)
    }

    companion object {
        private val logger = LogManager.getLogger()
    }
}
