package me.viniciusarnhold.altaria.events.interfaces

import sx.blah.discord.api.events.Event

/**
 * Created by Vinicius.

 * @since ${PROJECT_VERSION}
 */
interface IReceiver {

    val eventType: Class<out Event>

    fun disable()

}
