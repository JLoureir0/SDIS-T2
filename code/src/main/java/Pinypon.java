import pinypon.cli.CLI;
import pinypon.cli.parser.Parser;
import pinypon.listener.Listener;
import pinypon.utils.Defaults;
import java.util.HashMap;

public class Pinypon {
    public static void main(String[] args) {

        Parser cliParser = new Parser(args);
        HashMap<Parser.Option, Object> parsed = cliParser.parse();

        int port = Defaults.PORT;
        Object obj = parsed.get(Parser.Option.PORT);
        if (obj != null) {
            port = (int) obj;
        }

        Listener listener = new Listener(port);
        listener.run();

        CLI cliInterface = new CLI();
        cliInterface.run();
    }
}
