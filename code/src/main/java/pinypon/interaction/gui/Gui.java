package pinypon.interaction.gui;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pinypon.interaction.parser.Parser;
import pinypon.listener.Listener;
import pinypon.user.User;
import pinypon.utils.Defaults;

import java.net.SocketException;
import java.util.HashMap;

public class Gui extends Application {

    private User user;

    @Override
    public void init() throws Exception {
        super.init();

        String[] args = (String []) this.getParameters().getRaw().toArray();
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

        this.user = User.restore(userJsonPath);

        Listener listener = null;
        try {
            listener = new Listener(port);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        listener.run();
    }

    @Override
    public void start(Stage stage) throws Exception {
        if (this.user == null) {
            // Do the gui to create the user, generate the private and public keys and create the new user.
        }
        Group group = new Group();
        Scene scene = new Scene(group, 400, 300);
        stage.setTitle(Defaults.APP_NAME);
        stage.setScene(scene);
        stage.show();
    }
}
