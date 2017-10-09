package me.viniciusarnhold.altaria.events.utils

import me.viniciusarnhold.altaria.events.EventManager
import me.viniciusarnhold.altaria.events.interfaces.ICommandHandler
import org.apache.commons.cli.*
import org.jetbrains.annotations.Contract
import sx.blah.discord.api.IDiscordClient
import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.util.DiscordException
import sx.blah.discord.util.MessageBuilder
import sx.blah.discord.util.MissingPermissionsException
import sx.blah.discord.util.RateLimitException
import java.io.PrintWriter
import java.io.StringWriter
import java.util.*
import java.util.concurrent.ConcurrentSkipListSet

/**
 * Created by Vinicius.

 * @since ${PROJECT_VERSION}
 */
class Commands private constructor(private val mainCommand: String, private val options: Options, private val handler: ICommandHandler, private val commandInfo: String) : Comparable<Commands> {

    init {

        if (!this.options.hasOption("h")) {
            options.addOption(defaultHelpOption)
        } else {
            options.addOption(Option.builder()
                    .desc("prints this option")
                    .longOpt(if (this.options.hasLongOption("help")) "showHelp" else "help")
                    .build())
        }
        commandList.add(this)
    }

    fun info(): String {
        return commandInfo
    }

    fun mainCommand(): String {
        return mainCommand
    }

    fun options(): Options {
        return options
    }

    fun handler(): ICommandHandler {
        return handler
    }

    @Contract("null -> fail")
    @Throws(ParseException::class)
    fun parse(text: String): CommandLine {
        val splitedText = splitByWhitespace(text)
        return DefaultParser().parse(this.options, splitedText.toTypedArray<String>())
    }

    @Throws(RateLimitException::class, DiscordException::class, MissingPermissionsException::class)
    fun showHelpIfPresent(client: IDiscordClient, channnel: IChannel, cmd: CommandLine): Boolean {
        if (cmd.hasOption("h") || cmd.hasOption("help") || cmd.hasOption("showHelp")) {
            val writter = StringWriter(200)
            val pw = PrintWriter(writter)
            HelpFormatter()
                    .printHelp(pw, 200,
                            EventManager.MAIN_COMMAND_NAME + "  " + this.mainCommand,
                            this.commandInfo,
                            this.options,
                            3,
                            5, null,
                            true)
            MessageBuilder(client)
                    .withChannel(channnel)
                    .withQuote(writter.toString())
                    .send()
            return true
        }
        return false
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val command = o as Commands?
        return mainCommand == command!!.mainCommand && options == command.options
    }

    override fun hashCode(): Int {
        return Objects.hash(mainCommand, options)
    }

    override fun compareTo(o: Commands): Int {
        return if (this.mainCommand.compareTo(o.mainCommand) < 0) if (this.options == o.options) 0 else 1 else -1
    }

    companion object {

        private val commandList = ConcurrentSkipListSet(Comparator<Commands> { obj, o -> obj.compareTo(o) })

        fun of(mainCommand: String, options: Options, handler: ICommandHandler, commandInfo: String): Commands {
            return Commands(mainCommand, options, handler, commandInfo)
        }

        val allCommands: SortedSet<Commands>
            get() = Collections.unmodifiableSortedSet(commandList)

        val defaultHelpOption: Option
            @Contract(" -> !null")
            get() = Option("h", "help", false, "prints this message")

        @Contract("null -> fail")
        fun splitByWhitespace(args: String): List<String> {
            val matcher = Regexes.WHITESPACE_SPLIT.pattern().matcher(Objects.requireNonNull(args))

            //As seen on http://stackoverflow.com/a/7804472
            val list = ArrayList<String>()
            while (matcher.find()) {
                list.add(matcher.group(1).replace("\"", ""))
            }
            return list
        }
    }
}
