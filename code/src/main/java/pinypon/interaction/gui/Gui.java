package pinypon.interaction.gui;

import javafx.application.Application;
import javafx.application.Platform;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import pinypon.interaction.parser.Parser;
import pinypon.listener.Listener;
import pinypon.user.User;
import pinypon.utils.Defaults;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.List;

public class Gui extends Application {

    private Stage stage;
    private Scene registerLoadScene;
    private Scene restoreUserScene;
    private Scene chatScene;
    private Scene createUserScene;

    private Button loadProfileButton;
    private User user;
    private int port;
    private String userJsonPath;

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

        this.userJsonPath = Defaults.USER_JSON_PATH;
        obj = parsed.get(Parser.Option.USER_JSON_PATH);
        if (obj != null) {
            this.userJsonPath = (String) obj;
        }
        restoreUserScene();
        registerLoadScene();
        chatScene();
    }

    @Override
    public void start(Stage stage) throws Exception {

        this.stage = stage;

        stage.setTitle(Defaults.APP_NAME);

        File file = new File(this.userJsonPath);

        if (file.exists() && !file.isDirectory()) {
            stage.setScene(this.restoreUserScene);
        } else {
            stage.setScene(this.registerLoadScene);
        }
        stage.show();

        try {
            final Listener listener = new Listener(port);

            final Thread listenerThread = new Thread(listener);
            listenerThread.start();

            this.stage.setOnCloseRequest(windowEvent -> {
                try {
                    Platform.exit();
                    listener.interrupt();
                    listenerThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    System.exit(0);
                }
            });

        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    private void restoreUserScene() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Label passwordLabel = new Label("password:");
        grid.add(passwordLabel, 0, 1);

        PasswordField passwordField = new PasswordField();
        grid.add(passwordField, 1, 1);

        Button registerLoadButton = new Button("register");
        registerLoadButton.setOnAction(actionEvent -> this.stage.setScene(registerLoadScene));
        Button loginButton = new Button("login");
        loginButton.setOnAction(actionEvent -> {
            try {
                this.user = User.restore(this.userJsonPath, passwordField.getText());
                this.stage.setScene(chatScene);
            } catch (FileNotFoundException e) {
                simpleAlert(Alert.AlertType.ERROR, "User", "Bad Input", e.getMessage());
            } catch (InvalidKeySpecException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                simpleAlert(Alert.AlertType.ERROR, "User", "Bad Password", "Please insert a valid password");
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            }
        });
        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.BOTTOM_RIGHT);
        hbox.getChildren().add(loginButton);
        hbox.getChildren().add(registerLoadButton);
        grid.add(hbox, 1, 3);

        this.restoreUserScene = new Scene(grid);
    }

    private void registerLoadScene() {
        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(25, 25, 25, 25));

        Text welcome = new Text("Welcome");
        welcome.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        vbox.getChildren().add(welcome);

        Button createUserButton = new Button("Create User");
        createUserScene();
        createUserButton.setOnAction(actionEvent -> createUserShow());
        vbox.getChildren().add(createUserButton);

        this.loadProfileButton = new Button("Load profile");
        this.loadProfileButton.setOnAction(actionEvent -> loadProfileHandler());
        vbox.getChildren().add(this.loadProfileButton);

        this.registerLoadScene = new Scene(vbox);
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

    private void chatScene() {
//        this.chatScene = new Scene();
    }

    private void createUser(CreateUserFields fields) {
        try {
            this.user = new User(fields.usernameField.getText(), fields.passwordField.getText(), fields.jsonPathField.getText());
            if (!this.user.store()) {
                simpleAlert(Alert.AlertType.ERROR, "User", "Store", "Failed to store user data");
            }
            this.stage.setScene(chatScene);
        } catch (IllegalArgumentException e) {
            simpleAlert(Alert.AlertType.ERROR, "User", "Bad Input", e.getMessage());
        }
    }

    private void loadProfileHandler() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Files", "*"),
                new FileChooser.ExtensionFilter("json", "*.secret")
        );
        File file = fileChooser.showOpenDialog(this.stage);
        if (file != null) {
            try {
                String password = getPasswordModalStage();
                this.user = User.restore(file.getAbsolutePath(), password);
                this.stage.setScene(chatScene);
            } catch (FileNotFoundException e) {
                simpleAlert(Alert.AlertType.ERROR, "User", "Bad Input", e.getMessage());
            } catch (InvalidKeySpecException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                simpleAlert(Alert.AlertType.ERROR, "User", "Bad Password", "Please insert a valid password");
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            }
        }
    }

    private String getPasswordModalStage() {
        Stage stage = new Stage();

        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Account Restore");

        Label passwordLabel = new Label();
        passwordLabel.setText("Password");
        PasswordField passwordField = new PasswordField();
        Button closeButton = new Button("login");
        closeButton.setOnAction(e -> stage.close());

        VBox vbox = new VBox(10);
        vbox.getChildren().addAll(passwordLabel, passwordField, closeButton);
        vbox.setAlignment(Pos.CENTER_LEFT);

        Scene scene = new Scene(vbox);
        stage.setScene(scene);
        stage.showAndWait();
        return passwordField.getText();
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
}
