package pinypon.interaction.cli;

import pinypon.interaction.parser.Parser;
import pinypon.listener.Listener;
import pinypon.user.User;
import pinypon.utils.Defaults;

import java.io.FileNotFoundException;
import java.net.SocketException;
import java.util.HashMap;
import java.util.List;

public class CLI {

    private User user;

    public CLI(String[] args) {
        Parser cliParser = new Parser(args);
        HashMap<Parser.Option, Object> parsed = cliParser.parse();

        int port = Defaults.PORT;
        Object obj = parsed.get(Parser.Option.PORT);
        if (obj != null) {
            port = (int) obj;
        }

        String userJsonPath = Defaults.USER_JSON_PATH;
        obj = parsed.get(Parser.Option.USER_JSON_PATH);
        if (obj != null) {
            userJsonPath = (String) obj;
        }

        try {
            this.user = User.restore(userJsonPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }

        Listener listener = null;
        try {
            listener = new Listener(port);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        new Thread(listener).start();
    }

    public void run() {

    }
}
