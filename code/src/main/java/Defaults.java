import commandLineParser.CommandLineParser;

import java.util.HashMap;

public class Defaults {
    static final public HashMap<CommandLineParser.Option, Object> cliParsing;
    static {
        cliParsing = new HashMap<>();
        cliParsing.put(CommandLineParser.Option.SERVER_PORT, 54321);
    }
}
