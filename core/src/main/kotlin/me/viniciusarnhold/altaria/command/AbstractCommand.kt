package me.viniciusarnhold.altaria.command

import me.viniciusarnhold.altaria.command.interfaces.ICommand
import me.viniciusarnhold.altaria.utils.Actions
import sx.blah.discord.api.events.IListener
import sx.blah.discord.handle.impl.events.MessageReceivedEvent
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.util.MessageBuilder
import sx.blah.discord.util.RequestBuffer
import java.util.*

/**
 * Created by Vinicius.

 * @since ${PROJECT_VERSION}
 */
abstract class AbstractCommand : ICommand, IListener<MessageReceivedEvent> {

    protected var command: String

    protected var aliases: Set<String>

    protected var description: String

    protected var commandType: CommandType

    protected var permissions: EnumSet<UserPermissions>

    init {
        command = ""
        aliases = emptySet<String>()
        description = ""
        commandType = CommandType.GENERAL
        permissions = EnumSet.noneOf<UserPermissions>(UserPermissions::class.java)
    }

    override fun command(): String {
        return command
    }

    override fun aliases(): Set<String> {
        return aliases
    }

    override fun description(): String {
        return description
    }

    override fun type(): CommandType {
        return commandType
    }

    override fun permissions(): EnumSet<UserPermissions> {
        return permissions
    }

    protected fun isMyCommand(event: MessageReceivedEvent): Boolean {
        return !event.message.channel.isPrivate &&
                !event.message.author.isBot &&
                MessageUtils.isMyCommand(event.message, this)
    }

    companion object {

        fun enqueue(builder: MessageBuilder): RequestBuffer.RequestFuture<IMessage> {
            return RequestBuffer.request(Actions.wrap<IMessage>({ builder.send() }))
        }
    }
}
