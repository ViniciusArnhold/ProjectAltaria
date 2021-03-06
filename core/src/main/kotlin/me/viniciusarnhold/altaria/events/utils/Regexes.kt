package me.viniciusarnhold.altaria.events.utils

import java.util.regex.Pattern

/**
 * Created by Vinicius.

 * @since 1.0
 */
enum class Regexes(private val pattern: Pattern) {

    //Most of old versions and test cases are on: https://regex101.com/r/dpzIkV/2
    DEFAULT_COMMAND(Pattern.compile("^(?:\\s*)(!alt\\s*((?:::)?[A-Za-z0-9]+)+(\\s+\"[A-Za-z0-9'\\-:]*\")*)(?:\\s*)$", Pattern.CASE_INSENSITIVE)),

    //https://regex101.com/r/dpzIkV/3
    BOT_COMMAND_NO_ARGS(Pattern.compile("![aA][lL][tT]\\s+((?:(?:::)?[A-Za-z0-9]+)+)")),

    WHITESPACE_SPLIT(Pattern.compile("([^\"]\\S*|\".+?\")\\s*"));

    fun pattern(): Pattern {
        return pattern
    }
}
