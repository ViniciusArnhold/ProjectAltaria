package commands.external.imdb

import com.google.common.collect.ImmutableSet
import me.viniciusarnhold.altaria.apis.imdb.Imdb
import commands.AbstractMessageCommand
import commands.MessageUtils
import commands.UserPermissions
import me.viniciusarnhold.altaria.events.utils.Commands
import me.viniciusarnhold.altaria.utils.Actions
import org.apache.logging.log4j.LogManager
import sx.blah.discord.handle.impl.events.MessageReceivedEvent
import java.util.*

/**
 * Created by Vinicius.

 * @since ${PROJECT_VERSION}
 */
class ImdbSearchCommand : AbstractMessageCommand() {
    init {
        this.command = "IMDBSearch"
        this.aliases = ImmutableSet.of("SearchIMDB", "SearchTV", "SearchMovies", "SearchMovie")
        this.commandType = commands.CommandType.API
        this.description = "Search's on imdb by args"
        this.permissions = EnumSet.noneOf<UserPermissions>(UserPermissions::class.java)
    }

    /**
     * Called when the event is sent.

     * @param event The event object.
     */
    override fun handle(event: MessageReceivedEvent) {
        if (!isSearchCommand(event)) {
            return
        }
        val command = event.message.content.trim()
        logger.traceEntry("Received imdb search commands {}", command)
        val args = Commands.splitByWhitespace(command).toMutableList()
        try {
            if (args.size < 2) {
                MessageUtils.getDefaultRequestBuilder(event.message)
                        .doAction(Actions.ofSuccess {
                            MessageUtils.getMessageBuilder(event.message)
                                    .appendContent("Command must have parameters to query for")
                                    .send()
                        })
                        .execute()
                return
            }

            val query = args.drop(1).joinToString(" ")

            val builder = MessageUtils.getEmbedBuilder(event.message.author)

            val results = Imdb.api().getSearch(query, Locale.US)

            if (results.isEmpty()) {
                MessageUtils.getDefaultRequestBuilder(event.message)
                        .doAction(Actions.ofSuccess {
                            MessageUtils.getMessageBuilder(event.message)
                                    .appendContent("No results found")
                                    .send()
                        })
                        .execute()
                return
            }

            builder.withTitle("Query results")
                    .withDescription(query)

            var isFirst = true
            //Will never be null, we just checked

            results.entries.first().value
                    .asSequence()
                    .filterNot { it.isError }
                    .forEach {
                        if (isFirst) {
                            builder.withImage(it.image.url)
                            isFirst = false
                        }
                    }


        } catch (e: Exception) {
            logger.error(e)
        }

    }

    private fun isSearchCommand(event: MessageReceivedEvent): Boolean {
        return !event.message.channel.isPrivate &&
                !event.message.author.isBot &&
                MessageUtils.isMyCommand(event.message, this)
    }

    companion object {

        private val logger = LogManager.getLogger()
    }
}
