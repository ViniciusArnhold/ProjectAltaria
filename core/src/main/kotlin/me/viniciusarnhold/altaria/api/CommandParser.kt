package me.viniciusarnhold.altaria.api

import me.viniciusarnhold.altaria.api.config.PrefixConfiguration
import org.apache.commons.validator.routines.UrlValidator
import org.apache.logging.log4j.LogManager
import java.net.URL

/**
 * @author Vinicius Pegorini Arnhold.
 */
class CommandParser {

    companion object {
        val LOGGER = LogManager.getLogger()
        val WHITESPACE_PATTERN = "([^\"]\\S*|\".+?\")\\s*".toRegex()

        val URL_VALIDATOR = UrlValidator(arrayOf("http", "https"))
    }

    fun parse(prefix: PrefixConfiguration, cmdLine: String): ParsedCommand? {
        val words = WHITESPACE_PATTERN.findAll(cmdLine.trim()).mapNotNull { it.groups[1]?.value }.map { it.replace("\"", "") }
        return when {
            words.none() -> null
            else -> {
                val first = words.first()
                when {
                    !first.startsWith(prefix.current) -> null
                    else -> ParsedCommand(prefix.current, first.drop(prefix.current.length), me.viniciusarnhold.altaria.api.CommandArgs.from(words.drop(1).map { parseArg(it) }))
                }
            }
        }

    }

    private fun parseArg(arg: String): me.viniciusarnhold.altaria.api.Arg {
        arg.toLongOrNull()?.let {
            return me.viniciusarnhold.altaria.api.LongArg(it)
        }
        if (URL_VALIDATOR.isValid(arg)) {
            return me.viniciusarnhold.altaria.api.URLArg(URL(arg))
        }

        return me.viniciusarnhold.altaria.api.Arg(arg)
    }

}