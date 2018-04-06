package me.viniciusarnhold.altaria.api

import me.viniciusarnhold.altaria.api.config.PrefixConfiguration
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import sx.blah.discord.api.IShard
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.util.EmbedBuilder
import sx.blah.discord.util.MessageBuilder
import sx.blah.discord.util.RequestBuffer
import sx.blah.discord.util.RequestBuilder

/**
 * @author Vinicius Pegorini Arnhold.
 */
class MessageCommandContext(val command: ParsedCommand,
                            val event: MessageReceivedEvent,
                            val configuration: CommandConfiguration,
                            val bot: AltariaBot,
                            val logger: Logger = LogManager.getLogger()) {

    val shard: IShard = event.message.shard

    val message: IMessage = event.message

    private fun sendMessage(messageBuilder: MessageBuilder): RequestBuffer.RequestFuture<IMessage> {
        return enqueue(messageBuilder::send)
    }

    fun withMessage(): MessageBuilder {
        return MessageBuilder(event.client)
                .withChannel(event.channel)
    }

    fun withMessage(message: String): MessageBuilder {
        return withMessage().withContent(message)
    }

    fun reply(message: MessageBuilder): RequestBuffer.RequestFuture<IMessage> {
        return sendMessage(message)
    }

    fun reply(message: String): RequestBuffer.RequestFuture<IMessage> {
        return sendMessage(withMessage()
                .withContent(message))
    }

    fun reply(embedBuilder: EmbedBuilder): RequestBuffer.RequestFuture<IMessage> {
        return sendMessage(withMessage()
                .withEmbed(embedBuilder.build()))
    }

    fun replyInPrivate(embedBuilder: EmbedBuilder): RequestBuffer.RequestFuture<IMessage> {
        return sendMessage(withMessage()
                .withChannel(message.author.orCreatePMChannel)
                .withEmbed(embedBuilder.build()))
    }


    fun replyWithError(message: String, showHelp: Boolean = false): RequestBuffer.RequestFuture<IMessage> {
        val messageBuilder = when (showHelp) {
            true -> throw Error("not implemented")//TODO
            false -> MessageBuilder(event.client)
                    .withChannel(event.channel)
                    .withContent(message)
        }
        return sendMessage(messageBuilder)
    }

    fun withEmbed(): EmbedBuilder {
        return MessageUtils.getEmbedBuilder(event.author)
    }

    fun isPrivate(): Boolean {
        return event.channel.isPrivate
    }

    fun isFromBot(): Boolean {
        return event.author.isBot
    }

    fun <T> enqueue(action: () -> T): RequestBuffer.RequestFuture<T> {
        return RequestBuffer.request(action)
    }

    fun withRequest(): RequestBuilder {
        return RequestBuilder(event.client)
                .setAsync(true)
    }
}

abstract class CommandConfiguration {
    abstract fun prefix(): PrefixConfiguration
    abstract fun <E> getService(clazz: Class<E>): E
}

