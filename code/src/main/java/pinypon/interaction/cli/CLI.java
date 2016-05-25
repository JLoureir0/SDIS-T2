package pinypon.interaction.cli;

import pinypon.interaction.parser.Parser;
import pinypon.listener.Listener;
import pinypon.utils.Defaults;

import java.net.SocketException;
import java.util.HashMap;

public class CLI {

    private final String[] args;

    public CLI(String[] args) {
        this.args = args;
    }

    public void run() {
        Parser cliParser = new Parser(args);
        HashMap<Parser.Option, Object> parsed = cliParser.parse();

        int port = Defaults.PORT;
        Object obj = parsed.get(Parser.Option.PORT);
        if (obj != null) {
            port = (int) obj;
        }

        Listener listener = null;
        try {
            listener = new Listener(port);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        listener.run();
    }
}
