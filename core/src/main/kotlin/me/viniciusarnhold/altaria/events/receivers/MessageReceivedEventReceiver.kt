package me.viniciusarnhold.altaria.events.receivers

import me.viniciusarnhold.altaria.events.CommandDelegator
import me.viniciusarnhold.altaria.events.interfaces.IReceiver
import me.viniciusarnhold.altaria.events.utils.Regexes
import org.apache.logging.log4j.LogManager
import sx.blah.discord.api.events.Event
import sx.blah.discord.api.events.IListener
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent

/**
 * Created by Vinicius.

 * @since ${PROJECT_VERSION}
 */
class MessageReceivedEventReceiver private constructor() : IReceiver, IListener<MessageReceivedEvent> {

    override val eventType: Class<out Event>
        get() = MessageReceivedEvent::class.java

    override fun disable() {
        logger.info("Receiver {} was disabled.", javaClass.name)
    }

    /**
     * Called when the event is sent.

     * @param event The event object.
     */
    override fun handle(event: MessageReceivedEvent) {
        logger.traceEntry("MessageReceivedEventReceiver will handle {}.", { event.message })

        val text = event.message.content.trim { it <= ' ' }

        val someoneHandled: Boolean
        val matcher = Regexes.BOT_COMMAND_NO_ARGS.pattern().matcher(text)

        if (!matcher.find()) {
            logger.traceExit<String>("Message {} received but did not pass the commands regex.", text)
            return
        }
        logger.debug("Event receiver {} received commannd: {}", CLASS_NAME, matcher.group())

        someoneHandled = CommandDelegator.instance.degelateCommand(event, matcher.group(1), text)

        logger.traceExit("MessageReceivedEvent was handled: {} times.", someoneHandled)
    }

    companion object {

        private val logger = LogManager.getLogger()

        private val CLASS_NAME = MessageReceivedEventReceiver::class.java.simpleName

        val instace = MessageReceivedEventReceiver()

        fun logMessageEventReceived(event: MessageReceivedEvent, text: String) {}
    }

}

