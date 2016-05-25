import pinypon.interaction.cli.CLI;
import pinypon.interaction.gui.Gui;

import java.util.Locale;

import static javafx.application.Application.launch;

public class Pinypon {
    private static final String USAGE = "usage";

    public static void main(String[] args) {

        if (args.length == 0) {
            usage();
            System.exit(1);
        }
        args[0].toLowerCase(Locale.ROOT);
        switch(args[0]) {
            case "gui":
                launch(Gui.class, args);
                break;
            case "cli":
                new CLI(args).run();
                break;
            default:
                usage();
                System.exit(1);
        }
    }

    private static void usage() {
        System.err.println(USAGE);
    }
}
