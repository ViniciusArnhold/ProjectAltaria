package me.viniciusarnhold.altaria.events.handler

import me.viniciusarnhold.altaria.events.interfaces.ICommandHandler
import me.viniciusarnhold.altaria.events.utils.Commands
import me.viniciusarnhold.altaria.events.utils.EventUtils
import org.apache.commons.cli.Option
import org.apache.commons.cli.Options
import org.apache.commons.cli.ParseException
import org.apache.logging.log4j.LogManager
import sx.blah.discord.handle.impl.events.MessageReceivedEvent
import sx.blah.discord.util.DiscordException
import sx.blah.discord.util.MessageBuilder
import sx.blah.discord.util.MissingPermissionsException
import sx.blah.discord.util.RateLimitException

/**
 * Created by Vinicius.

 * @since ${PROJECT_VERSION}
 */
class SimpleCommandHandler private constructor() : ICommandHandler {

    @Throws(DiscordException::class, MissingPermissionsException::class, RateLimitException::class)
    private fun sendHelloWorld(event: MessageReceivedEvent, isMention: Boolean, to: Array<String>?) {
        val builder = StringBuilder(40)
        if (isMention) {
            if (to == null) {
                builder.append(event.message.author.mention())
                        .append(" ")
                        .append("Hello!")
            } else {
                builder.append("Hello")
                for (str in to) {
                    builder.append(" ")
                            .append(str)
                }
                builder.append("!")
            }
        } else {
            builder.append("Hello World!")
        }
        MessageBuilder(event.client)
                .withChannel(event.message.channel)
                .withContent(builder.toString())
                .send()
    }

    @Throws(RateLimitException::class, DiscordException::class, MissingPermissionsException::class)
    override fun handle(event: MessageReceivedEvent, command: String, matchedText: String): Boolean {

        if (HELLO_COMMAND.mainCommand().equals(command, ignoreCase = true)) {
            try {
                val cmd = HELLO_COMMAND.parse(matchedText)

                if (HELLO_COMMAND.showHelpIfPresent(event.client, event.message.channel, cmd)) {
                    return true
                }

                val isMention = cmd.hasOption("m")
                val to = cmd.getOptionValues("m")

                sendHelloWorld(event, isMention, to)

            } catch (e: ParseException) {
                logger.info("Parsing failed", e)
                EventUtils.sendIncorrectUsageMessage(event.client, event.message.channel, HELLO_COMMAND.mainCommand(), e.message ?: "Unknown")
            }

            return true
        }
        return false
    }

    override val handableCommands: List<Commands>
        get() = listOf(HELLO_COMMAND)

    companion object {

        private val logger = LogManager.getLogger()
        private val HELLO_COMMAND: Commands
        val instance = SimpleCommandHandler()

        init {
            val options = Options()
            options.addOption(Option.builder("m")
                    .longOpt("mention")
                    .type(String::class.java)
                    .desc("When this argument is passed the message becomes '@<args or caller> Hello!'")
                    .optionalArg(true)
                    .build())
            HELLO_COMMAND = Commands.of("Hello", options, instance, "Answers with `Hello `args`!` or @Person Hello!")
        }
    }
}
