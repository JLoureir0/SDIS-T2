package pinypon.interaction.gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.abstractj.kalium.keys.KeyPair;
import pinypon.interaction.parser.Parser;
import pinypon.listener.Listener;
import pinypon.user.User;
import pinypon.utils.Defaults;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.SocketException;
import java.util.HashMap;
import java.util.List;

public class Gui extends Application {

    private Stage stage;
    private Scene registerLoadScene;

    private Button createUserButton;
    private Scene createUserScene;
    private static class CreateUserFields {
        public final TextField usernameField;
        public final PasswordField passwordField;
        public final TextField jsonPathField;

        public CreateUserFields(TextField usernameField, PasswordField passwordField, TextField jsonPathField) {
            this.usernameField = usernameField;
            this.passwordField = passwordField;
            this.jsonPathField = jsonPathField;
        }
    }
    private Button loadProfileButton;

    private Scene chatScene;

    private User user;
    private static int port;

    @Override
    public void init() throws Exception {
        super.init();

        List<String> args = this.getParameters().getRaw();
        Parser cliParser = new Parser(args.toArray(new String[args.size()]));
        HashMap<Parser.Option, Object> parsed = cliParser.parse();

        this.port = Defaults.PORT;
        Object obj = parsed.get(Parser.Option.PORT);
        if (obj != null) {
            this.port = (int) obj;
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
        }

    }

    @Override
    public void start(Stage stage) throws Exception {

        this.stage = stage;

        stage.setTitle(Defaults.APP_NAME);

        if (this.user == null) {
            VBox vbox = new VBox(10);
            vbox.setAlignment(Pos.CENTER);
            vbox.setPadding(new Insets(25, 25, 25, 25));

            Text welcome = new Text("Welcome");
            welcome.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
            vbox.getChildren().add(welcome);

            this.createUserButton = new Button("Create User");
            createUserScene();
            this.createUserButton.setOnAction(actionEvent -> createUserShow());
            vbox.getChildren().add(this.createUserButton);

            this.loadProfileButton = new Button("Load profile");
            this.loadProfileButton.setOnAction(actionEvent -> loadProfileHandler());
            vbox.getChildren().add(this.loadProfileButton);

            this.registerLoadScene = new Scene(vbox);
            stage.setScene(this.registerLoadScene);
            stage.show();
        }

        Listener listener = null;
        try {
            listener = new Listener(port);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        new Thread(listener).start();
    }

    private void createUserShow() {
        this.stage.setScene(createUserScene);
    }

    private void createUserScene() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text sceneTitle = new Text("Create you account");
        sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(sceneTitle, 0, 0, 2, 1);

        Label userNameLabel = new Label("username:");
        grid.add(userNameLabel, 0, 1);

        TextField usernameField = new TextField();
        grid.add(usernameField, 1, 1);

        Label passwordLabel = new Label("password:");
        grid.add(passwordLabel, 0, 2);

        PasswordField passwordField = new PasswordField();
        grid.add(passwordField, 1, 2);

        Label jsonPathLabel = new Label("saveProfileTo:");
        grid.add(jsonPathLabel, 0, 3);

        TextField jsonPathField = new TextField();
        grid.add(jsonPathField, 1, 3);

        Button createUserButton = new Button("create");
        createUserButton.setOnAction(actionEvent -> createUser(new CreateUserFields(usernameField, passwordField, jsonPathField)));
        Button registerLoadButton = new Button("Go to main page");
        registerLoadButton.setOnAction(actionEvent -> this.stage.setScene(this.registerLoadScene));
        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.BOTTOM_RIGHT);
        hbox.getChildren().add(createUserButton);
        hbox.getChildren().add(registerLoadButton);
        grid.add(hbox, 1, 5);

        this.createUserScene = new Scene(grid);
    }

    private void createUser(CreateUserFields fields) {
        KeyPair keyPair = new KeyPair();
        try {
            this.user = new User(fields.usernameField.getText(), fields.passwordField.getText(), fields.jsonPathField.getText(), keyPair.getPublicKey(), keyPair.getPrivateKey());
            this.stage.setScene(chatScene);
        } catch (IllegalArgumentException e) {
            simpleAlert(Alert.AlertType.ERROR, "User", "Bad Input", e.getMessage());
        }
    }

    private void loadProfileHandler() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Files", "*.*"),
                new FileChooser.ExtensionFilter("json", "*.json")
        );
        File file = fileChooser.showOpenDialog(this.stage);
        if (file != null) {
            try {
                this.user = User.restore(file.getAbsolutePath());
                this.stage.setScene(chatScene);
            } catch (FileNotFoundException e) {
                simpleAlert(Alert.AlertType.ERROR, "User", "Bad Input", e.getMessage());
            }
        }
    }

    private void simpleAlert(Alert.AlertType type, String title, String header, String context) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(context);
        alert.setX(this.stage.getX() + this.stage.getWidth() / 2 - alert.getWidth() / 2);
        alert.setY(this.stage.getY() + this.stage.getHeight() / 2 - alert.getHeight() / 2);
        alert.showAndWait();
    }
}
