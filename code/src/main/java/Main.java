import commandLineInterface.CommandLineInterface;
import commandLineParser.CommandLineParser;
import org.abstractj.kalium.keys.KeyPair;
import peer.Server;

import java.io.IOException;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) {

        CommandLineParser cliParser = new CommandLineParser(args);
        HashMap<CommandLineParser.Option, Object> parsed = cliParser.parse();

        int server_port = (int) utils.Defaults.cliParsing.get(CommandLineParser.Option.SERVER_PORT);
        Object obj = parsed.get(CommandLineParser.Option.SERVER_PORT);
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

        CommandLineInterface cliInterface = new CommandLineInterface();
        cliInterface.run();
    }
}
