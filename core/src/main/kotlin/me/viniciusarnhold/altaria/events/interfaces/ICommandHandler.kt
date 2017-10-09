package me.viniciusarnhold.altaria.events.interfaces

import me.viniciusarnhold.altaria.events.utils.Commands
import sx.blah.discord.handle.impl.events.MessageReceivedEvent
import sx.blah.discord.util.DiscordException
import sx.blah.discord.util.MissingPermissionsException
import sx.blah.discord.util.RateLimitException

/**
 * Created by Vinicius.

 * @since 1.0
 */
interface ICommandHandler {

    @Throws(RateLimitException::class, DiscordException::class, MissingPermissionsException::class)
    fun handle(event: MessageReceivedEvent, command: String, matchedText: String): Boolean

    val handableCommands: List<Commands>?

}
