package me.viniciusarnhold.altaria.command.external.imdb;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.omertron.imdbapi.search.SearchObject;
import me.viniciusarnhold.altaria.apis.imdb.Imdb;
import me.viniciusarnhold.altaria.command.AbstractCommand;
import me.viniciusarnhold.altaria.command.CommandType;
import me.viniciusarnhold.altaria.command.MessageUtils;
import me.viniciusarnhold.altaria.command.UserPermissions;
import me.viniciusarnhold.altaria.events.utils.Commands;
import me.viniciusarnhold.altaria.utils.Actions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.util.EmbedBuilder;

import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Vinicius.
 *
 * @since ${PROJECT_VERSION}
 */
public class ImdbSearchCommand extends AbstractCommand {

    private static final Logger logger = LogManager.getLogger();

    public ImdbSearchCommand() {
        this.command = "IMDBSearch";
        this.aliases = ImmutableSet.of("SearchIMDB", "SearchTV", "SearchMovies", "SearchMovie");
        this.commandType = CommandType.API;
        this.description = "Search's on imdb by args";
        this.permissions = EnumSet.noneOf(UserPermissions.class);
    }

    /**
     * Called when the event is sent.
     *
     * @param event The event object.
     */
    @Override
    public void handle(@NotNull MessageReceivedEvent event) {
        if (!isSearchCommand(event)) {
            return;
        }
        @NotNull String command = event.getMessage().getContent().trim();
        logger.traceEntry("Received imdb search command {}", command);
        @NotNull List<String> args = Commands.splitByWhitespace(command);
        try {
            if (args.size() < 2) {
                MessageUtils.getDefaultRequestBuilder(event.getMessage())
                            .doAction(Actions.ofSuccess(() ->
                                    MessageUtils.getMessageBuilder(event.getMessage())
                                                .appendContent("Command must have parameters to query for")
                                                .send()))
                            .execute();
                return;
            }
            args.remove(0);

            String query = args.stream().collect(Collectors.joining(" "));

            EmbedBuilder builder = MessageUtils.getEmbedBuilder(event.getMessage().getAuthor());

            Map<String, List<SearchObject>> results = Imdb.api().getSearch(query, Locale.US);

            if (results.isEmpty()) {
                MessageUtils.getDefaultRequestBuilder(event.getMessage())
                            .doAction(Actions.ofSuccess(() ->
                                    MessageUtils.getMessageBuilder(event.getMessage())
                                                .appendContent("No results found")
                                                .send()))
                            .execute();
                return;
            }

            builder.withTitle("Query results")
                   .withDescription(query);

            boolean isFirst = true;
            int count = 0;

            //Will never be null, we just checked
            //noinspection ConstantConditions
            for (@NotNull SearchObject searchObject : Iterables.getFirst(results.entrySet(), null).getValue()) {
                if (searchObject.isError()) {
                    continue;
                }
                if (isFirst) {
                    builder.withImage(searchObject.getImage().getUrl());
                    isFirst = false;
                }

                //builder.appendField("", "", )

            }


        } catch (Exception e) {
            logger.error(e);
        }

    }

    private boolean isSearchCommand(@NotNull MessageReceivedEvent event) {
        return !event.getMessage().getChannel().isPrivate() &&
                !event.getMessage().getAuthor().isBot() &&
                MessageUtils.isMyCommand(event.getMessage(), this);
    }
}
