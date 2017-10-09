package me.viniciusarnhold.altaria.events.handler

import com.google.common.collect.Lists
import me.viniciusarnhold.altaria.events.EventManager
import me.viniciusarnhold.altaria.events.StatsCounter
import me.viniciusarnhold.altaria.events.interfaces.ICommandHandler
import me.viniciusarnhold.altaria.events.utils.Commands
import me.viniciusarnhold.altaria.events.utils.EventUtils
import me.viniciusarnhold.altaria.utils.TimeUtils
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.HelpFormatter
import org.apache.commons.cli.Options
import org.apache.commons.cli.ParseException
import org.apache.logging.log4j.LogManager
import sx.blah.discord.handle.impl.events.MessageReceivedEvent
import sx.blah.discord.util.DiscordException
import sx.blah.discord.util.MessageBuilder
import sx.blah.discord.util.MissingPermissionsException
import sx.blah.discord.util.RateLimitException
import java.io.PrintWriter
import java.io.StringWriter
import java.lang.management.ManagementFactory
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 * Created by Vinicius.

 * @since ${PROJECT_VERSION}
 */
class BotInfoCommandHandler private constructor() : ICommandHandler {

    @Throws(RateLimitException::class, DiscordException::class, MissingPermissionsException::class)
    private fun showInfo(event: MessageReceivedEvent, verbose: Boolean) {
        val builder = StringBuilder(if (verbose) 300 else 150)
        val manager = EventManager.instance
        builder.append(manager.name)
                .append(" - Version: ")
                .append(manager.version)
                .append(lineSeparator())
                .append("Created by: ")
                .append(manager.author)
                .append(lineSeparator())
        if (verbose) {
            builder.append("Uptime: ").append(ManagementFactory.getRuntimeMXBean().uptime)
                    .append(" (Since: ").append(DATE_TIME_FORMATTER.format(ZonedDateTime.now())).append(')')
                    .append(lineSeparator())
                    .append("Commands Handled: ").append(StatsCounter.commandsHandled())
                    .append(lineSeparator())
                    .append("Commands handle time :")
                    .append(lineSeparator())
                    .append("Max: ").append(TimeUtils.formatToString(StatsCounter.maxCommandHandleTime())).append(' ')
                    .append("Mean: ").append(TimeUtils.formatToString(StatsCounter.meanCommandHandleTime())).append(' ')
                    .append("Min: ").append(TimeUtils.formatToString(StatsCounter.minCommandHandleTime())).append(' ')
                    .append(lineSeparator())
        }
        builder.append("   Have fun!")

        MessageBuilder(event.client)
                .appendQuote(builder.toString())
                .withChannel(event.message.channel)
                .send()
    }

    @Throws(RateLimitException::class, DiscordException::class, MissingPermissionsException::class)
    private fun showHelp(event: MessageReceivedEvent, verbose: Boolean) {
        if (verbose) {

            val writer = StringWriter(1000)
            val pWriter = PrintWriter(writer)
            pWriter.append("Heres  everything I can do!")
            pWriter.append(lineSeparator())

            for (command in Commands.allCommands) {
                HelpFormatter().printHelp(
                        pWriter,
                        100,
                        EventManager.MAIN_COMMAND_NAME + ' ' + command.mainCommand(),
                        command.info(),
                        command.options(),
                        5,
                        3,
                        "",
                        true)
                pWriter.append(lineSeparator())
            }
            pWriter.close()

            MessageBuilder(event.client)
                    .appendQuote(writer.toString())
                    .withChannel(event.message.author.orCreatePMChannel)
                    .send()

            MessageBuilder(event.client)
                    .appendContent(event.message.author.mention())
                    .appendContent("   ")
                    .appendContent("Check your PM`s!")
                    .withChannel(event.message.channel)
                    .send()
            return
        }


        val commands = Commands.allCommands
        val writer = StringWriter(1000)
        val pWriter = PrintWriter(writer)
                .append("List of commands this bot accepts, all commands must start with ")
                .append(EventManager.MAIN_COMMAND_NAME)
                .append(lineSeparator())
                .append("Commands names are case-insensitive.")
                .append(lineSeparator())

        for (command in commands) {
            HelpFormatter().printUsage(pWriter,
                    100,
                    EventManager.MAIN_COMMAND_NAME + ' ' + command.mainCommand(),
                    command.options())
            pWriter.append("  ")
                    .append(command.info())
                    .append(lineSeparator())
        }
        MessageBuilder(event.client)
                .appendQuote(writer.toString())
                .withChannel(event.message.channel)
                .send()

        pWriter.close()
    }

    @Throws(RateLimitException::class, DiscordException::class, MissingPermissionsException::class)
    override fun handle(event: MessageReceivedEvent, commandName: String, matchedText: String): Boolean {

        if (INFO_COMMAND.mainCommand().equals(commandName, ignoreCase = true)) {
            val parser = DefaultParser()
            try {
                val cmd = parser.parse(INFO_COMMAND.options(), Commands.splitByWhitespace(matchedText).toTypedArray<String>())

                if (INFO_COMMAND.showHelpIfPresent(event.client, event.message.channel, cmd)) {
                    return true
                }
                showInfo(event, cmd.hasOption("v"))

            } catch (e: ParseException) {
                LOGGER.info("Parsing failed", e)
                EventUtils.sendIncorrectUsageMessage(event.client, event.message.channel, INFO_COMMAND.mainCommand(), e.message ?: "Unknown")
            }

            return true
        }

        if (HELP_COMMAND.mainCommand().equals(commandName, ignoreCase = true)) {
            val parser = DefaultParser()
            try {
                val cmd = parser.parse(HELP_COMMAND.options(), Commands.splitByWhitespace(matchedText).toTypedArray<String>())

                if (HELP_COMMAND.showHelpIfPresent(event.client, event.message.channel, cmd)) {
                    return true
                }
                showHelp(event, cmd.hasOption("v"))

            } catch (e: ParseException) {
                LOGGER.info("Parsing failed", e)
                EventUtils.sendIncorrectUsageMessage(event.client, event.message.channel, HELP_COMMAND.mainCommand(), e.message ?: "Unknown")
            }

            return true
        }
        return false
    }

    override val handableCommands: List<Commands>
        get() = commands


    private fun lineSeparator(): Char {
        return '\n'
    }

    companion object {

        private val LOGGER = LogManager.getLogger()

        val instance = BotInfoCommandHandler()


        private val DATE_TIME_FORMATTER: DateTimeFormatter


        private val HELP_COMMAND: Commands


        private val INFO_COMMAND: Commands
        private val commands: List<Commands>

        init {
            DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm:ss")
        }

        init {
            var options = Options()
                    .addOption("v", "verbose", false, "shows the -help for each command, will send the message by PM because of size")
            HELP_COMMAND = Commands.of("Help", options, instance, "Shows this!")

            options = Options()
                    .addOption("v", "verbose", false, "Shows aditional stats, like uptime etc.")
            INFO_COMMAND = Commands.of("Info", options, instance, "Shows some information about bot.")
            commands = Lists.newArrayList(HELP_COMMAND, INFO_COMMAND)
        }
    }
}

