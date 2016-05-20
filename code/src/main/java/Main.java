import pinypon.cli.CLI;
import pinypon.cli.parser.Parser;
import pinypon.Server;
import pinypon.utils.Defaults;

import java.io.IOException;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) {

        Parser cliParser = new Parser(args);
        HashMap<Parser.Option, Object> parsed = cliParser.parse();

        int server_port = (int) Defaults.cliParsing.get(Parser.Option.SERVER_PORT);
        Object obj = parsed.get(Parser.Option.SERVER_PORT);
        if (obj != null) {
            server_port = (int) obj;
        }

        System.out.println(server_port);
        Server server = new Server(server_port);

        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        CLI cliInterface = new CLI();
        cliInterface.run();
    }
}
