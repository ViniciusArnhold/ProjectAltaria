package me.viniciusarnhold.altaria.api.message.command

import me.viniciusarnhold.altaria.api.Arg
import me.viniciusarnhold.altaria.api.CommandParser
import me.viniciusarnhold.altaria.api.config.PrefixConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on

/**
 * @author Vinicius Pegorini Arnhold.
 */
internal class CommandParserTest : Spek({
    describe("a non-configured CommandParser") {
        val parser = CommandParser()
        val prefixConfig = PrefixConfiguration("!")
        val prefix = prefixConfig.current

        on("a simple cmdline") {
            val name = "ping"
            val args = listOf("pong", "going")
            val cmdline = givenACommand(prefix, name, args)

            val command = parser.parse(prefixConfig, cmdline).assertNonNull()

            it("should parse its name") {
                assertThat(command.name).isEqualTo(name)
            }

            it("should parse its prefix") {
                assertThat(command.prefix).isEqualTo(prefix)
            }

            it("should parse its args") {
                assertThat(command.args.map(Arg::value)).containsExactlyElementsOf(args)
            }
        }

    }
})

fun givenACommand(prefix: String, name: String, args: Iterable<String>) = "${prefix}${name} ${args.joinToString(" ")}"