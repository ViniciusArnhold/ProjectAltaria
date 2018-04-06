package me.viniciusarnhold.altaria.commands.common

import me.viniciusarnhold.altaria.api.MessageCommandContext
import me.viniciusarnhold.altaria.api.deleteIn
import java.lang.StringBuilder
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.TimeUnit

/**
 * Created by Vinicius.

 * @since 1.0
 */
class EigthBallCommand : me.viniciusarnhold.altaria.api.AbstractMessageCommand() {

    override val command = "8ball"
    override val aliases = setOf("magicBall", "eightball")
    override val description = "Asks the magic eight ball for its wisdom."

    override fun onCommand(ctx: MessageCommandContext) {
        if (ctx.isFromBot() || ctx.isPrivate()) return

        val prefix = StringBuilder(":8ball: says:")
        when {
            ctx.command.args.isEmpty() -> ctx.replyWithError("$prefix I gonna need some questions pal.", true)
            else -> ctx.reply("$prefix ${ANSWERS[ThreadLocalRandom.current().nextInt(0, ANSWERS.size)]}")
        }

        ctx.event.message.deleteIn(1, TimeUnit.HOURS)
    }

    companion object {
        private val ANSWERS = listOf("It is certain",
                "It is decidedly so",
                "Without a doubt",
                "Yes definitely",
                "You may rely on it",
                "As I see it, yes",
                "Most likely",
                "Outlook good",
                "Yes",
                "Signs point to yes",
                "Reply hazy try again",
                "Ask again later",
                "Better not tell you now",
                "Cannot predict now",
                "Concentrate and ask again",
                "Don't count on it",
                "My sources say no",
                "My reply is no",
                "Outlook not so good",
                "Very doubtful")
    }
}
