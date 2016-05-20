package pinypon.utils;

import pinypon.cli.parser.Parser;

import java.util.HashMap;

public class Defaults {
    static final public HashMap<Parser.Option, Object> cliParsing;
    static {
        cliParsing = new HashMap<>();
        cliParsing.put(Parser.Option.SERVER_PORT, 54321);
    }

    public static final int NOUNCE_SIZE = 24;
}
