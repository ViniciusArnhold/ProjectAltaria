package me.viniciusarnhold.altaria.events.handler.thirdparty;

import com.robrua.orianna.api.core.AsyncRiotAPI;
import com.robrua.orianna.api.core.RiotAPI;
import com.robrua.orianna.type.core.common.Region;
import com.robrua.orianna.type.core.common.Season;
import com.robrua.orianna.type.core.stats.AggregatedStats;
import com.robrua.orianna.type.core.stats.PlayerStatsSummary;
import com.robrua.orianna.type.core.stats.PlayerStatsSummaryType;
import com.robrua.orianna.type.core.summoner.Summoner;
import de.vandermeer.asciitable.v2.RenderedTable;
import de.vandermeer.asciitable.v2.V2_AsciiTable;
import de.vandermeer.asciitable.v2.render.V2_AsciiTableRenderer;
import de.vandermeer.asciitable.v2.render.WidthAbsoluteEven;
import de.vandermeer.asciitable.v2.themes.V2_E_TableThemes;
import me.viniciusarnhold.altaria.events.interfaces.ICommandHandler;
import me.viniciusarnhold.altaria.events.utils.Commands;
import me.viniciusarnhold.altaria.utils.configuration.ConfigurationManager.Configurations;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Created by Vinicius.
 *
 * @since ${PROJECT_VERSION}
 */
public class LeagueCommandHandler implements ICommandHandler {

    private static final Logger logger = LogManager.getLogger();
    private static final Pattern LADDER_VALUE_PATTERN = Pattern.compile("ladder=\\w+", Pattern.CASE_INSENSITIVE);
    @NotNull
    private static LeagueCommandHandler ourInstance = new LeagueCommandHandler();

    static {
        RiotAPI.setAPIKey(Configurations.LeagueOauthToken.value());
        RiotAPI.setRegion(Region.KR);
        AsyncRiotAPI.setAPIKey(Configurations.LeagueOauthToken.value());
        AsyncRiotAPI.setRegion(Region.KR);
    }

    private LeagueCommandHandler() {

    }

    @NotNull
    public static LeagueCommandHandler getInstance() {
        return ourInstance;
    }

    private void showRandedStats(@NotNull MessageReceivedEvent event, final String ladder, final String name) throws RateLimitException, DiscordException, MissingPermissionsException {
        /*if (Objects.isNull(name) || StringUtils.isEmpty(name)) {
            answerWithError(event, "Nome de summoner invalido.");
            return;
        }
        if (Objects.isNull(ladder) || StringUtils.isEmpty(ladder)) {
            answerWithError(event, "Nome de ladder invalido.");
            return;
        }
        @NotNull final PlayerStatsSummaryType type;
        try {
            type = PlayerStatsSummaryType.valueOf(ladder);
        } catch (IllegalArgumentException e) {
            LOGGER.debug(() -> "Ladde " + ladder + " nao encontrada", e);
            answerWithError(event, MessageFormat.format("Ladder {0} nao encontrado.", ladder));
            return;
        }

        @NotNull final Summoner summoner;
        try {
            summoner = RiotAPI.getSummonerByName(name);
        } catch (APIException ioob) {
            LOGGER.debug(() -> "Summoner " + name + "Nao encontrado", ioob);
            answerWithError(event, MessageFormat.format("Summoner {0} nao encontrado.", name));
            return;
        }*/
        Summoner summoner = RiotAPI.getSummonerByName("Hide on bush");
        PlayerStatsSummary stats = summoner.getStats(Season.SEASON2016).get(PlayerStatsSummaryType.RankedSolo5x5);
        if (Objects.isNull(stats)) {
            answerWithError(event, MessageFormat.format("Summoner {0} nao tem dados para ladder {1}.", name, ladder));
            return;
        }
        @NotNull V2_AsciiTable table = new V2_AsciiTable();
        table.addRule();
        table.addRow(MessageFormat.format("Summoner stats for {0} from the {1} ladder.", summoner, ladder));
        table.addRule();
        int wins = stats.getWins();
        int losses = stats.getLosses();
        @NotNull Double winratio = (losses != 0 && wins != 0) ? ((wins * 100) / losses) : Double.POSITIVE_INFINITY;
        table.addRow("Wins", wins, "Losses", losses, "Win Ratio", winratio.toString());
        table.addRule();
        table.addRow("Averages");
        AggregatedStats agr = stats.getAggregatedStats();
        table.addRow("Kills", agr.getAverageKills(), "Deaths", agr.getAverageDeaths(), "Assists", agr.getAverageAssists());
        table.addRow("Maxes");
        table.addRow("Kills", agr.getMaxKills(), "Deaths", agr.getMaxDeaths(), "Assists", agr.getMaxAssists(), "Killing Spree", agr.getMaxKillingSpree());
        table.addRule();
        table.addRow("Misc");
        table.addRow("Maior jogo", agr.getMaxTimePlayed(), "Penta Kills", agr.getTotalPentaKills(), "Quadra Kills", agr.getTotalQuadraKills());
        table.addRule();

        @NotNull V2_AsciiTableRenderer renderer = new V2_AsciiTableRenderer();
        renderer.setTheme(V2_E_TableThemes.UTF_LIGHT.get());
        renderer.setWidth(new WidthAbsoluteEven(200));
        RenderedTable result = renderer.render(table);

        new MessageBuilder(event.getClient())
                .appendQuote(result.toString())
                .withChannel(event.getMessage().getChannel())
                .send();
    }


    private void answerWithError(@NotNull MessageReceivedEvent event, @NotNull ErrorMessages error) throws RateLimitException, DiscordException, MissingPermissionsException {
        answerWithError(event, error.getErrorMessage());
    }

    private void answerWithError(@NotNull MessageReceivedEvent event, String message) throws RateLimitException, DiscordException, MissingPermissionsException {
        new MessageBuilder(event.getClient())
                .appendContent("Error")
                .appendQuote(message)
                .withChannel(event.getMessage().getChannel())
                .send();
    }

    @Override
    public boolean handle(MessageReceivedEvent event, String command, String matchedText) {
        /*IMessage message = event.getMessage();
        Matcher matcher = Regexes.SIMPLE_ARGS.getRegex().matcher(message.getContent().trim());

        if (matcher.find()) {
            EventUtils.logMessageEventReceived(event, this.getClass());
            try {
                String[] command = matcher.group(1).split("::");
                if (command.length < 2 || !StringUtils.startsWithIgnoreCase(command[0], "league")) {
                    return;
                }
                switch (command[1].toLowerCase()) {
                    case "stats":
                        if (command.length < 3) {
                            answerWithError(event, ErrorMessages.MISSING_LADDER);
                            return;
                        } else if (!LADDER_VALUE_PATTERN.matcher(command[2]).find()) {
                            answerWithError(event, ErrorMessages.WRONG_FORMAT_LADDER);
                            return;
                        }
                        String ladder = command[2].split("=")[1];
                        showRandedStats(event, ladder, matcher.group(2));
                    default:
                        break;
                }
            } catch (Exception e) {
                LOGGER.error("Failed during " + this.getClass().getSimpleName() + " event handling.", e);
            }
        }*/
        return false;
    }

    @Nullable
    @Override
    public List<Commands> getHandableCommands() {
        return null;
    }

    private enum ErrorMessages {
        MISSING_LADDER("Commands League::Profile called without ladder, correct usage is League::Profile::Ladder=<LADDER> \"PLAYER\""),
        WRONG_FORMAT_LADDER("Commands League::Stats::Ladder=<Num> sent in wrong format. ");


        private final String errorMessage;

        ErrorMessages(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
