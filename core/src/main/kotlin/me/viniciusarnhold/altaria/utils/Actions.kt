package me.viniciusarnhold.altaria.utils

import me.viniciusarnhold.altaria.core.BotManager
import sx.blah.discord.util.DiscordException
import sx.blah.discord.util.MissingPermissionsException
import sx.blah.discord.util.RateLimitException
import sx.blah.discord.util.RequestBuffer.IRequest
import sx.blah.discord.util.RequestBuffer.IVoidRequest
import sx.blah.discord.util.RequestBuilder.IRequestAction


/**
 * Created by Vinicius.

 * @since ${PROJECT_VERSION}
 */
object Actions {


    @Throws(RateLimitException::class, MissingPermissionsException::class, DiscordException::class)
    fun ofSuccess(action: () -> Unit): () -> Boolean = {
        action()
        true
    }

    @Throws(RateLimitException::class, MissingPermissionsException::class, DiscordException::class)
    fun ofFailure(action: () -> Unit): IRequestAction = IRequestAction {
        action()
        false
    }

    @Throws(RateLimitException::class, MissingPermissionsException::class, DiscordException::class)
    fun ofSuccess(): IRequestAction = IRequestAction { true }

    @Throws(RateLimitException::class, MissingPermissionsException::class, DiscordException::class)
    fun ofFailure(): IRequestAction = IRequestAction { false }

    fun <T> wrap(supplier: () -> T): IRequest<T> = IRequest {
        try {
            supplier()
        } catch (e: DiscordException) {
            BotManager.LOGGER.error(e)
            throw RuntimeException(e)
        } catch (e: MissingPermissionsException) {
            BotManager.LOGGER.error(e)
            throw RuntimeException(e)
        }
    }

    fun wrap(supplier: () -> Unit): IVoidRequest = IVoidRequest {
        try {
            supplier()
        } catch (e: DiscordException) {
            BotManager.LOGGER.error(e)
            throw RuntimeException(e)
        } catch (e: MissingPermissionsException) {
            BotManager.LOGGER.error(e)
            throw RuntimeException(e)
        }
    }
}
