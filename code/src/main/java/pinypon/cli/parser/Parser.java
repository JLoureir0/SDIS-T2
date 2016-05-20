package pinypon.cli.parser;

import java.util.HashMap;

public class Parser {

    static final private String DHT_PORT_LONG = "--dhtport";
    static final private String DHT_PORT_SHORT = "-dp";
    static final private String CHAT_PORT_LONG = "--chatport";
    static final private String CHAT_PORT_SHORT = "-cp";

    final private String []args;
    private HashMap<Option, Object> parsed = new HashMap<>();

    public enum Option {
        DHT_PORT,
        CHAT_PORT
    }

    public Parser(String []args) {
        this.args = args;
    }

    public HashMap<Option, Object> parse() {
        int args_length = this.args.length;
        for (int index = 0; index < args_length; ++index) {
            switch(this.args[index]) {
                case DHT_PORT_LONG:
                case DHT_PORT_SHORT:
                    if ((index = index + 1) >= args_length) {
                        throw new IllegalArgumentException(args[index - 1] + " port");
                    }
                    int dht_port = Integer.parseInt(args[index]);
                    if (dht_port < 1 || dht_port > 65535) {
                        throw new IllegalArgumentException(args[index - 1] + " 1-65535");
                    }
                    this.parsed.put(Option.DHT_PORT, dht_port);
                    break;
                case CHAT_PORT_LONG:
                case CHAT_PORT_SHORT:
                    if ((index = index + 1) >= args_length) {
                        throw new IllegalArgumentException(args[index - 1] + " port");
                    }
                    int chat_port = Integer.parseInt(args[index]);
                    if (chat_port < 1 || chat_port > 65535) {
                        throw new IllegalArgumentException(args[index - 1] + " 1-65535");
                    }
                    this.parsed.put(Option.CHAT_PORT, chat_port);
                    break;
                default:
                    throw new IllegalStateException(args[index] + " invalid option");
            }
        }
        return this.parsed;
    }
}
