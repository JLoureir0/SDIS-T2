import java.util.HashMap;

public class CommandLineParser {

    static final private String PORT_LONG = "--listenerport";
    static final private String PORT_SHORT = "-lp";

    final private String []args;
    private HashMap<Option, Object> parsed = new HashMap<>();

    public enum Option {
        SERVER_PORT
    }

    public CommandLineParser(String []args) {
        this.args = args;
    }

    public HashMap<Option, Object> parse() {
        int args_length = this.args.length;
        for (int index = 0; index < args_length; ++index) {
            switch(this.args[index]) {
                case PORT_LONG:
                case PORT_SHORT:
                    if ((index = index + 1) >= args_length) {
                        throw new IllegalArgumentException(args[index - 1] + " port");
                    }
                    int server_port = Integer.parseInt(args[index]);
                    if (server_port < 1 || server_port > 65535) {
                        throw new IllegalArgumentException(args[index - 1] + " 1-65535");
                    }
                    this.parsed.put(Option.SERVER_PORT, server_port);
                    break;
                default:
                    throw new IllegalStateException(args[index] + " invalid option");
            }
        }
        return this.parsed;
    }
}
