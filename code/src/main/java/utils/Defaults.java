package utils;

import commandLineParser.CommandLineParser;

import java.util.HashMap;

public class Defaults {
    public static final HashMap<CommandLineParser.Option, Object> cliParsing;
    static {
        cliParsing = new HashMap<>();
        cliParsing.put(CommandLineParser.Option.SERVER_PORT, 54321);
    }

    public static final int NOUNCE_SIZE = 24;
}
