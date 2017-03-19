package me.viniciusarnhold.altaria.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.json.jackson2.JacksonFactory;
import me.viniciusarnhold.altaria.apis.HttpManager;
import me.viniciusarnhold.altaria.apis.objects.XKCDComic;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

/**
 * Created by Vinicius.
 *
 * @since ${PROJECT_VERSION}
 */
public class Tests {
    /*

        public static void t() {

            V2_AsciiTable table = new V2_AsciiTable();

            table.addStrongRule();

            table.addRule();
            table.addRow(null, null, null, null, null, null, null, null);
            table.addRow(MessageFormat.format("Summoner stats for {0} from the {1} ladder.", "SuperrSonico", "Platina"), null, null, null, null, null, null, null);
            table.addRule();
            int wins = 80;
            int losses = 40;
            Double winratio = (losses != 0 && wins != 0) ? ((wins * 100) / losses) : Double.POSITIVE_INFINITY;
            table.addRow("Wins", wins, "Losses", losses, "Win Ratio", winratio.toString(), null, null);
            table.addRule();
            table.addRow("Averages", null, null, null, null, null, null, null);
            table.addRow("Kills", 12, "Deaths", 6, "Assists", 18, null, null);
            table.addRow("Maxes", null, null, null, null, null, null, null);
            table.addRow("Kills", 1200, "Deaths", 600, "Assists", 1800, "Killing Spree", 17);
            table.addRule();
            table.addRow("Misc", null, null, null, null, null, null, null);
            table.addRow("Maior jogo", 110, "Penta Kills", 1, "Quadra Kills", 2, null, null);
            table.addRule();

            V2_AsciiTableRenderer renderer = new V2_AsciiTableRenderer();
            renderer.setTheme(V2_E_TableThemes.UTF_LIGHT.get());
            renderer.setWidth(new WidthAbsoluteEven(100));
            RenderedTable result = renderer.render(table);

            System.out.println(result);
        }

        private static final Commands HELP_COMMAND;

        private static final Commands INFO_COMMAND;

        static {
            Options options = new Options()
                    .addOption("v", "verbose", false, "shows help for each command")
                    .addOption(Commands.getDefaultHelpOption());
            HELP_COMMAND = Commands.of("Help", options, BotInfoCommandHandler.getInstance());

            options = new Options()
                    .addOption("v", "verbose", false, "shows aditional info.")
                    .addOption(Commands.getDefaultHelpOption());
            INFO_COMMAND = Commands.of("Info", options, BotInfoCommandHandler.getInstance());
        }

        private void showInfo(MessageReceivedEvent event) throws RateLimitException, DiscordException, MissingPermissionsException {
            StringBuilder builder = new StringBuilder(150);
            EventManager manager = EventManager.getInstance();
            builder.append(manager.getName())
                    .append(" - Version: ")
                    .append(manager.getVersion())
                    .append("\n")
                    .append("Created by: ")
                    .append(manager.getAuthor())
                    .append("\n")
                    .append("   Have fun!");

            System.out.println(builder.toString());
        }

        private void showHelp(MessageReceivedEvent event) throws RateLimitException, DiscordException, MissingPermissionsException {
            */
/* Set<Commands> commands = Lists.newArrayList(INFO_COMMAND,HELP_COMMAND);
        StringBuilder builder = new StringBuilder(commands.size() * 75);
        builder.append(MessageFormat.format("List of commands this bot accepts, all commands must start with {0}", EventManager.MAIN_COMMAND_NAME));
        builder.append("Commands are case-insensitive.");
        for (Commands cmd :
                commands) {
            builder.append("\n")
                    .append(cmd.comma())
                    .append(" - ")
                    .append(cmd.getHelpText()); *//*


    }

    public static void main(String[] args) {

        CommandLineParser parser = new DefaultParser();

        String info = "!alt Info -v -v";

        Options options = new Options()
                .addOption(Option.builder("l")
                        .longOpt("limit")
                        .hasArg()
                        .argName("size")
                        .type(Integer.class)
                        .desc("Limits the result size, max: 5, default: 3")
                        .required(false)
                        .build())
                .addOption(Option.builder("t")
                        .longOpt("text")
                        .type(String.class)
                        .required()
                        .desc("The Text to be searched")
                        .hasArg()
                        .numberOfArgs(Option.UNLIMITED_VALUES)
                        .argName("Input")
                        .build())
                .addOption(Commands.getDefaultHelpOption());

        String googleFull = "!alt Pesquisa::Google -l 3 --text=\"Pesquisa Google\"";

        String googleMin = "!alt Pesquisa::Google -t \"Pesquisa Google\"";

        String googleInv = "!alt Pesquisa::Google --text \"Pesquisa no google lulz\" -h";

        Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(googleInv);
        List<String> help = new ArrayList<>();
        while(m.find()) {
            help.add(m.group(1).replace("\"",""));
        }



        String[] asa = new String[]{
                "!alt",
                "Pesquisa::Google",
                "--limit",
                "3",
                "--text",
                "Pesquisa Google"
        };

        try {
            if (parser.parse(options,help.toArray(new String[help.size()])).hasOption("h")) {
                new HelpFormatter().printHelp("!alt Pesquisa::Google","Searchs google",options,"Footer in 2017 lulz",true);
            }

                CommandLine cmd = parser.parse(options, asa);
            cmd = parser.parse(options, googleFull.split(" "));

            new HelpFormatter().printHelp("!alt Pesquisa::Google",
                    "Makes a search on google and messages back with links to the results"
                    , options,
                    "Please try again with the correct usage.",
                    true);


            CommandLine cmdMin = parser.parse(options, asa);


            CommandLine cmdInv = parser.parse(options, googleInv.split(" "));


            cmd.getOptionValue("v");
            new HelpFormatter().printHelp(EventManager.MAIN_COMMAND_NAME + INFO_COMMAND.mainCommand(), INFO_COMMAND.options());

            System.out.println(cmd);
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }
*/
    private static final Header SHORTNEN_HEADER = new BasicHeader("Content-Type", "application/json");
    private static final String SHORTNEN_API_URL = "https://www.googleapis.com/urlshortener/v1/url";
    private static final String LONG_URL_PARAM = "longUrl";

 /*
    public static void main(String... args) {

        Script scriptService = GoogleClientServiceFactory.getInstance().getScriptService();

        String name = "Form from bot script 2.";
        String[] authors = {"viniciuspegoriniarnhold@gmail.com", "altaria.bot@gmail.com"};
        GoogleCommandHandler.Question[] questions = {
                new GoogleCommandHandler.TextQuestion("Question 1", "This is a help"),
                new GoogleCommandHandler.ChoiceQuestion("Question 5, Choice 1", "Help of choice 1", "Choice 1", "Choice 2", "Is it tho?"),
                new GoogleCommandHandler.TextQuestion("Question 2", "This is a help of 2"),
                new GoogleCommandHandler.ChoiceQuestion("Question 6, Choice 2", "Help of choice 1", "Choice 1", "Choice 2", "Is it tho?"),
                new GoogleCommandHandler.TextQuestion("Question 3", "This is a help of 3"),
        };

        ExecutionRequest executionRequest = new ExecutionRequest()
                .setFunction("createForm")
                .setParameters(Arrays.asList(name, authors, questions))
                .setDevMode(true);

        try {
            Operation op = scriptService
                    .scripts()
                    .run("1LgmCML3Qb07cbCtRqUYb3MMMKXfC7_QJRv-6Eg7FGWUy3ha5G-lIMiSY", executionRequest)
                    .execute();

            if (op.getError() != null) {

                System.out.println("error = [" + op.getError() + "]");

            }


            op.getResponse();

            System.out.println("getResponse = [" + op.getResponse() + "]");

        } catch (IOException e) {
            e.printStackTrace();
        }

/*
        Surveys surveys = GoogleClientServiceFactory.getInstance().getSurveyService();

        Survey survey = new Survey();

        survey.setTitle("Test YO")
                .setDescription("Desc YO")
                .setAudience(new SurveyAudience()
                        .setCountry("US")
                        .setLanguages(Arrays.asList("en-US")))
                .setOwners(Arrays.asList("viniciuspegoriniarnhold@gmail.com"))
                .setQuestions(Arrays.asList(
                        new SurveyQuestion()
                                .setQuestion("Question 2")
                                .setType("openEnded"),
                        new SurveyQuestion()
                                .setQuestion("Question 2")
                                .setType("openEnded")
                                           ))
                .setWantedResponseCount(100);


        try {
            Survey surveyAns = surveys.surveys().insert(survey).execute();


            System.out.println("surveyAns = [" + surveyAns + "]");
            System.out.println("surveyAns.getSurveyUrlId() = " + surveyAns.getSurveyUrlId());


        } catch (IOException e) {
            e.printStackTrace();
        }
        */


    public static void main(String[] args) throws Exception {

        // input = new SyndFeedInput();
        //SyndFeed feed = input.build(new XmlReader(new URL("http://stackoverflow.com/questions/tagged/java-8")));


        Request request = new Request.Builder()
                .url("http://xkcd.com/" + "/info.0.json")
                .build();

        try (Response response = HttpManager.getInstance().getDefaultClient().newCall(request).execute()) {

            if (response.isSuccessful()) {
                String json = response.body().string();

                ObjectMapper mapper = new ObjectMapper();

                XKCDComic comic = mapper.readValue(json, XKCDComic.class);

                XKCDComic xkcd = JacksonFactory.getDefaultInstance().fromString(json, XKCDComic.class);

                System.out.println();

            }
        }

        //elements.select("src")

    }
}

//System.out.println("feed = " + feed);

