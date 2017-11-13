package me.viniciusarnhold.altaria.command.pool

import sx.blah.discord.handle.obj.IMessage
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by Vinicius.

 * @since ${PROJECT_VERSION}
 */
class Pool internal constructor(private val options: Map<String, String>, private val message: IMessage, private val time: Long, private val type: Pool.Type) {

    private val poolID = POOL_COUNTER.getAndIncrement()

    fun options(): Map<String, String> {
        return options
    }

    fun time(): Long {
        return time
    }

    fun message(): IMessage {
        return message
    }


    fun id(): Int {
        return poolID
    }


    fun type(): Type {
        return this.type
    }

    enum class Type {

        MULTI,
        SINGLE

    }

    companion object {

        private val POOL_COUNTER = AtomicInteger(1)
    }
}
