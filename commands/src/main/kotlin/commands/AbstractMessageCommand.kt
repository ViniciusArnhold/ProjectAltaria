package commands

import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import java.util.*

/**
 * Created by Vinicius.

 * @since ${PROJECT_VERSION}
 */
abstract class AbstractMessageCommand protected constructor(override val command: String,
                                                            override val aliases: Set<String>,
                                                            override val description: String,
                                                            override val type: EnumSet<commands.CommandType>,
                                                            override val permissions: EnumSet<commands.UserPermissions>) : commands.IMessageCommand {

    protected fun isCommandForMe(event: MessageReceivedEvent): Boolean {
        return !event.message.channel.isPrivate &&
                !event.message.author.isBot &&
                commands.MessageUtils.isMyCommand(event.message, this)
    }
}
