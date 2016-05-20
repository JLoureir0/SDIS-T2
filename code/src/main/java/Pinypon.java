import pinypon.cli.CLI;
import pinypon.cli.parser.Parser;
import pinypon.listener.ChatListener;
import pinypon.listener.DHTListener;
import pinypon.utils.Defaults;
import java.util.HashMap;

public class Pinypon {
    public static void main(String[] args) {

        Parser cliParser = new Parser(args);
        HashMap<Parser.Option, Object> parsed = cliParser.parse();

        int dhtPort = Defaults.DHT_PORT;
        Object obj = parsed.get(Parser.Option.DHT_PORT);
        if (obj != null) {
            dhtPort = (int) obj;
        }

        int chatPort = Defaults.CHAT_PORT;
        obj = parsed.get(Parser.Option.CHAT_PORT);
        if (obj != null) {
            chatPort = (int) obj;
        }

        DHTListener dhtListener = new DHTListener(dhtPort);
        ChatListener chatListener = new ChatListener(chatPort);

        try {
            dhtListener.run();
            chatListener.run();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        CLI cliInterface = new CLI();
        cliInterface.run();
    }
}
