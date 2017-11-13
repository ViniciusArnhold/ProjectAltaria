package commands

import me.viniciusarnhold.altaria.utils.Actions
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.util.MessageBuilder
import sx.blah.discord.util.RequestBuffer


fun MessageBuilder.enqueue(): RequestBuffer.RequestFuture<IMessage> {
    return RequestBuffer.request(Actions.wrap<IMessage>({ this.send() }))
}