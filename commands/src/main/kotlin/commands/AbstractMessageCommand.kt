package me.viniciusarnhold.altaria.command

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import java.util.*

/**
 * Created by Vinicius.

 * @since ${PROJECT_VERSION}
 */
abstract class AbstractMessageCommand protected constructor(override val command: String,
                                                            override val aliases: Set<String>,
                                                            override val description: String,
                                                            override val type: EnumSet<me.viniciusarnhold.altaria.command.CommandType>,
                                                            override val permissions: EnumSet<me.viniciusarnhold.altaria.command.UserPermissions>) : me.viniciusarnhold.altaria.command.IMessageCommand {

    protected fun isCommandForMe(event: MessageReceivedEvent): Boolean {
        return !event.message.channel.isPrivate &&
                !event.message.author.isBot &&
                me.viniciusarnhold.altaria.command.MessageUtils.isMyCommand(event.message, this)
    }
}
