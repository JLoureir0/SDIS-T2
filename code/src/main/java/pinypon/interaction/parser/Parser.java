package pinypon.interaction.parser;

import java.util.HashMap;

public class Parser {

    static final private String PORT_LONG = "--port";
    static final private String PORT_SHORT = "-p";
    static final private String TRACKER_IP_LONG = "--trackerIp";
    static final private String TRACKER_IP_SHORT = "-ti";
    static final private String TRACKER_PORT_LONG = "--trackerPort";
    static final private String TRACKER_PORT_SHORT = "-tp";
    static final private String USER_JSON_PATH_LONG = "--user-json";
    static final private String USER_JSON_PATH_SHORT = "-uj";

    final private String[] args;
    private HashMap<Option, Object> parsed = new HashMap<>();

    public Parser(String[] args) {
        this.args = args;
    }

    public HashMap<Option, Object> parse() {
        int args_length = this.args.length;
        for (int index = 1; index < args_length; ++index) {
            switch (this.args[index]) {
                case PORT_LONG:
                case PORT_SHORT:
                    if ((index = index + 1) >= args_length) {
                        throw new IllegalArgumentException(args[index - 1] + " port");
                    }
                    int port = Integer.parseInt(args[index]);
                    if (port < 1 || port > 65535) {
                        throw new IllegalArgumentException(args[index - 1] + " 1-65535");
                    }
                    this.parsed.put(Option.PORT, port);
                    break;
                case TRACKER_IP_LONG:
                case TRACKER_IP_SHORT:
                    if ((index = index + 1) >= args_length) {
                        throw new IllegalArgumentException(args[index - 1] + " ip");
                    }
                    this.parsed.put(Option.TRACKER_IP, args[index]);
                    break;
                case TRACKER_PORT_LONG:
                case TRACKER_PORT_SHORT:
                    if ((index = index + 1) >= args_length) {
                        throw new IllegalArgumentException(args[index - 1] + " port");
                    }
                    port = Integer.parseInt(args[index]);
                    if (port < 1 || port > 65535) {
                        throw new IllegalArgumentException(args[index - 1] + " 1-65535");
                    }
                    this.parsed.put(Option.TRACKER_PORT, port);
                    break;
                case USER_JSON_PATH_LONG:
                case USER_JSON_PATH_SHORT:
                    if ((index = index + 1) >= args_length) {
                        throw new IllegalArgumentException(args[index - 1] + " userJsonPath");
                    }
                    this.parsed.put(Option.USER_JSON_PATH, args[index]);
                    break;
                default:
                    throw new IllegalStateException(args[index] + " invalid option");
            }
        }
        return this.parsed;
    }

    public enum Option {
        PORT,
        TRACKER_IP,
        TRACKER_PORT,
        USER_JSON_PATH
    }
}
