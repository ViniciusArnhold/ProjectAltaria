package me.viniciusarnhold.altaria.api

/**
 * @author Vinicius Pegorini Arnhold.
 */
class ParsedCommand(val prefix: String, val name: String, val args: me.viniciusarnhold.altaria.api.CommandArgs) {

    val full = "${prefix}${name} ${args.joinToString("")}"

    override fun toString(): String {
        return full
    }
}