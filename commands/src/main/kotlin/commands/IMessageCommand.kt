package me.viniciusarnhold.altaria.command

import sx.blah.discord.api.events.IListener

/**
 * Created by Vinicius.

 * @since ${PROJECT_VERSION}
 */
interface IMessageCommand : ICommand, IListener<sx.blah.discord.handle.impl.events.MessageReceivedEvent> {

    fun describeUsage(builder: HelperBuilder)
}
