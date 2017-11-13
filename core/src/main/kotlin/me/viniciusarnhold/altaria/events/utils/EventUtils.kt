package me.viniciusarnhold.altaria.events.utils

import me.viniciusarnhold.altaria.events.EventManager
import org.apache.commons.lang3.StringUtils.SPACE
import org.apache.http.HttpStatus
import org.jsoup.HttpStatusException
import sx.blah.discord.api.IDiscordClient
import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.util.DiscordException
import sx.blah.discord.util.MessageBuilder
import sx.blah.discord.util.MissingPermissionsException
import sx.blah.discord.util.RateLimitException

/**
 * Created by Vinicius.

 * @since ${PROJECT_VERSION}
 */
class EventUtils private constructor() {

    init {
        throw Error()
    }

    companion object {

        @Throws(RateLimitException::class, DiscordException::class, MissingPermissionsException::class)
        fun sendConnectionErrorMessage(client: IDiscordClient, channel: IChannel, command: String, message: String?, httpe: HttpStatusException) {
            var problem = if (message != null) message + "\n" else ""
            if (httpe.statusCode == HttpStatus.SC_SERVICE_UNAVAILABLE) {
                problem += "Service unavailable, please try again latter."
            } else if (httpe.statusCode == HttpStatus.SC_FORBIDDEN) {
                problem += "Acess dennied."
            } else if (httpe.statusCode == HttpStatus.SC_NOT_FOUND) {
                problem += "Not Found"
            } else {
                problem += httpe.statusCode.toString() + SPACE + httpe.message
            }

            MessageBuilder(client)
                    .appendContent("Error during HTTP Connection ", MessageBuilder.Styles.BOLD)
                    .appendContent("\n")
                    .appendContent(EventManager.MAIN_COMMAND_NAME, MessageBuilder.Styles.BOLD)
                    .appendContent(SPACE)
                    .appendContent(command, MessageBuilder.Styles.BOLD)
                    .appendContent("\n")
                    .appendContent(problem, MessageBuilder.Styles.BOLD)
                    .withChannel(channel)
                    .send()
        }

        @Throws(RateLimitException::class, DiscordException::class, MissingPermissionsException::class)
        fun sendIncorrectUsageMessage(client: IDiscordClient, channel: IChannel, command: String, reason: String) {
            MessageBuilder(client)
                    .appendContent("Incorrect usage of commands: ", MessageBuilder.Styles.BOLD)
                    .appendContent(command, MessageBuilder.Styles.BOLD)
                    .appendContent("\n")
                    .appendContent(reason, MessageBuilder.Styles.BOLD)
                    .withChannel(channel)
                    .send()
        }
    }

}
