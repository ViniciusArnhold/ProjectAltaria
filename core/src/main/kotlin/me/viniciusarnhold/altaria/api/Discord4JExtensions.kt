package me.viniciusarnhold.altaria.api

import me.viniciusarnhold.altaria.utils.Actions
import me.viniciusarnhold.altaria.utils.Timers
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.util.MessageBuilder
import sx.blah.discord.util.RequestBuffer
import sx.blah.discord.util.RequestBuilder
import java.util.concurrent.TimeUnit


fun MessageBuilder.enqueue(): RequestBuffer.RequestFuture<IMessage> {
    return RequestBuffer.request(Actions.wrap<IMessage>({ this.send() }))
}

fun MessageBuilder.sendAndDeleteIn(time: Long, unit: TimeUnit): RequestBuffer.RequestFuture<IMessage> {
    val sendRequest = RequestBuffer.request(RequestBuffer.IRequest { this.send() })

    RequestBuffer.request {
        Timers.MessageDeletionService.schedule(sendRequest.get(), time, unit)
    }

    return sendRequest
}

fun IMessage.deleteIn(time: Long, unit: TimeUnit) {
    return Timers.MessageDeletionService.schedule(this, time, unit)
}

fun IMessage.deleteNow(): RequestBuffer.RequestFuture<Void>? {
    return RequestBuffer.request {
        this.delete()
    }
}

fun RequestBuilder.thenRun(action: () -> Any): RequestBuilder {
    return this.andThen {
        action()
        true
    }
}