package pinypon.interaction.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.abstractj.kalium.keys.PublicKey;
import pinypon.handler.chat.ipeer.IPeerHandler;
import pinypon.interaction.parser.Parser;
import pinypon.listener.ChatListener;
import pinypon.user.Friend;
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
import java.util.Iterator;
import java.util.List;

import static org.abstractj.kalium.encoders.Encoder.HEX;

public class Gui extends Application {

    private Stage stage;
    private Scene registerLoadScene;
    private Scene restoreUserScene;
    private Scene chatScene;
    private Scene createUserScene;
    private ActiveFriendTextArea activeFriendTextArea = new ActiveFriendTextArea();
    private ListView<Friend> friendsListView;
    private HashMap<String, TextArea> friendsTextAreas = new HashMap<>();
    private IPeerHandler iPeerHandler;
    private TextField messageField;
    private BorderPane chatBorderPane;
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

        this.iPeerHandler = new IPeerHandler();
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
            final ChatListener chatListener = new ChatListener(this.port);
            final GuiMessagePrinter guiMessagePrinter = new GuiMessagePrinter(this, chatListener.getPeerHandler().getMessagesToPrint());
            chatListener.start();
            guiMessagePrinter.start();

            this.stage.setOnCloseRequest(windowEvent -> {
                try {
                    if (this.user != null) {
                        this.user.store();
                    }
                    Platform.exit();
                    chatListener.kill();
                    guiMessagePrinter.kill();
                    this.iPeerHandler.kill();
                    guiMessagePrinter.join();
                    chatListener.join();
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
        passwordField.setOnAction(actionEvent -> login(passwordField));
        grid.add(passwordField, 1, 1);

        Button registerLoadButton = new Button("register");
        registerLoadButton.setOnAction(actionEvent -> this.stage.setScene(registerLoadScene));
        Button loginButton = new Button("login");
        loginButton.setOnAction(actionEvent -> login(passwordField));
        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.BOTTOM_RIGHT);
        hbox.getChildren().add(loginButton);
        hbox.getChildren().add(registerLoadButton);
        grid.add(hbox, 1, 3);

        this.restoreUserScene = new Scene(grid);
    }

    private void login(PasswordField passwordField) {
        try {
            this.user = User.restore(this.userJsonPath, passwordField.getText());
            chatSetup();
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

        Label jsonPathLabel = new Label("Save profile to:");
        grid.add(jsonPathLabel, 0, 3);
        Button jsonPathButton = new Button(this.userJsonPath);
        jsonPathButton.setOnAction(actionEvent -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("All Files", "*"),
                    new FileChooser.ExtensionFilter("User file", "*.secret")
            );
            File file = fileChooser.showOpenDialog(this.stage);
            if (file != null) {
                this.userJsonPath = file.getAbsolutePath();
            }
        });
        grid.add(jsonPathButton, 1, 3);

        Button createUserButton = new Button("create");
        createUserButton.setOnAction(actionEvent -> createUser(new CreateUserFields(usernameField, passwordField)));
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
        this.friendsListView = new ListView<>();
        this.friendsListView.setEditable(false);
        this.friendsListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.chatBorderPane = new BorderPane();
        this.chatBorderPane.setRight(friendsListView);
        this.chatScene = new Scene(chatBorderPane);
    }

    private void chatSetup() {
        HashMap<String, Friend> friends = this.user.getFriends();
        boolean gotFirst = false;
        Friend firstFriend = null;
        if (friends != null) {
            Iterator<HashMap.Entry<String, Friend>> friendIterator = friends.entrySet().iterator();
            while (friendIterator.hasNext()) {
                Friend friend = friendIterator.next().getValue();
                if (!gotFirst) {
                    firstFriend = friend;
                    gotFirst = true;
                }
                this.friendsListView.getItems().add(friend);
                this.friendsTextAreas.putIfAbsent(friend.getEncodedPublicKey(), createChatTextArea());
            }
        }
        friendsListView.getSelectionModel().selectedItemProperty().addListener((observable, OldFriend, newFriend) -> {
            TextArea friendTextArea = this.friendsTextAreas.get(newFriend.getEncodedPublicKey());
            this.activeFriendTextArea.set(friendTextArea, newFriend);
            this.chatBorderPane.setCenter(friendTextArea);
        });
        if (firstFriend != null) {
            TextArea firstFriendTextArea = this.friendsTextAreas.get(firstFriend.getEncodedPublicKey());
            this.friendsListView.getSelectionModel().select(firstFriend);
            this.activeFriendTextArea.set(firstFriendTextArea, firstFriend);
            this.chatBorderPane.setCenter(firstFriendTextArea);
        } else {
            TextArea defaultTextArea = createChatTextArea();
            this.activeFriendTextArea.set(defaultTextArea, null);
            this.chatBorderPane.setCenter(defaultTextArea);
        }

        this.messageField = new TextField();
        this.messageField.setOnAction(actionEvent -> {
            try {
                messageHandler();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        this.chatBorderPane.setBottom(this.messageField);
        this.stage.setScene(chatScene);
    }

    private void messageHandler() throws IOException, InterruptedException {
        if (this.messageField.getText().isEmpty()) {
            return;
        }

        String message = new String(messageField.getText());
        if (message.charAt(0) == '/') {
            String[] filteredMessage = message.split(Defaults.WHITESPACE_REGEX);
            switch (filteredMessage[0]) {
                case "/add":
                    if (filteredMessage.length != 3) {
                        simpleAlert(Alert.AlertType.ERROR, "User", "Bad input", "expected arguments: username publicKey");
                        throw new IllegalArgumentException("Expecting two more arguments: username publicKey");
                    }
                    try {
                        addFriend(filteredMessage[1], filteredMessage[2]);
                        // Friend Request call ipeer
                    } catch (RuntimeException e) {
                        simpleAlert(Alert.AlertType.ERROR, "User", "Bad input", "invalid publicKey");
                        throw new IllegalArgumentException("invalid publicKey");
                    }
                    break;
                case "/id":
                    if (filteredMessage.length == 1) {
                        writeToActiveTextArea(this.user.getEncodedPublicKey());
                    } else if (filteredMessage.length == 2) {
                        switch (filteredMessage[1]) {
                            case "mine":
                                writeToActiveTextArea(this.user.getEncodedPublicKey());
                                break;
                            case "his":
                                Friend friend = this.activeFriendTextArea.getFriend();
                                if (friend != null) {
                                    writeToActiveTextArea(friend.getEncodedPublicKey());
                                } else {
                                    simpleAlert(Alert.AlertType.ERROR, "User", "Bad state", "no user connected to this textArea");
                                }
                                break;
                            default:
                                simpleAlert(Alert.AlertType.ERROR, "User", "id bad input", "Bad option");
                                throw new IllegalArgumentException("Bad option");
                        }
                    } else {
                        simpleAlert(Alert.AlertType.ERROR, "User", "Bad input", "(mine|his|)");
                        throw new IllegalArgumentException("No more arguments expected");
                    }
                    break;
                default:
                    simpleAlert(Alert.AlertType.ERROR, "User", "Bad input", "Bad option");
                    throw new IllegalArgumentException("Bad option");
            }
        } else {
            TextArea activeTextArea = this.activeFriendTextArea.getTextArea();
            activeTextArea.appendText(this.user.getUsername() + ": " + message + "\n");
            this.messageField.clear();
            User user = this.user;
            Friend friend = this.activeFriendTextArea.getFriend();
            this.iPeerHandler.sendMessage(this.user, friend, message);
        }
    }

    private void addFriend(String username, String encodedPublicKey) {

        try {
            new PublicKey(HEX.decode(encodedPublicKey));
        } catch (RuntimeException e) {
            simpleAlert(Alert.AlertType.ERROR, "User", "Bad input", "invalid public key");
            return;
        }
        if (encodedPublicKey.equals(this.user.getEncodedPublicKey())) {
            simpleAlert(Alert.AlertType.ERROR, "User", "Bad input", "cant add yourself as a friend!");
            return;
        }
        if (this.user.getFriend(encodedPublicKey) != null) {
            simpleAlert(Alert.AlertType.ERROR, "User", "Bad input", "friend already added!");
            return;
        }
        Friend friend = new Friend(username, encodedPublicKey);
        this.user.addFriend(friend);

        this.friendsListView.getItems().add(friend);
        TextArea friendTextArea = createChatTextArea();
        this.friendsTextAreas.putIfAbsent(friend.getEncodedPublicKey(), friendTextArea);

        if (user.getFriends().size() == 1) {
            this.activeFriendTextArea.set(friendTextArea, friend);
            this.chatBorderPane.setCenter(friendTextArea);
            this.friendsListView.getSelectionModel().select(friend);
        }

        this.messageField.clear();
    }

    private void writeToActiveTextArea(String message) {
        TextArea textArea = this.activeFriendTextArea.getTextArea();
        if (textArea == null) {
            return;
        }
        textArea.appendText(message + "\n");
        this.messageField.clear();
    }

    private TextArea createChatTextArea() {
        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        return textArea;
    }

    private void createUser(CreateUserFields fields) {
        try {
            this.user = new User(fields.usernameField.getText(), fields.passwordField.getText(), this.userJsonPath);
            if (!this.user.store()) {
                simpleAlert(Alert.AlertType.ERROR, "User", "Store", "Failed to store user data");
            }
            chatSetup();
        } catch (IllegalArgumentException e) {
            simpleAlert(Alert.AlertType.ERROR, "User", "Bad Input", e.getMessage());
        }
    }

    private void loadProfileHandler() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Files", "*"),
                new FileChooser.ExtensionFilter("User file", "*.secret")
        );
        File file = fileChooser.showOpenDialog(this.stage);
        if (file != null) {
            try {
                String password = getPasswordModalStage();
                this.user = User.restore(file.getAbsolutePath(), password);
                chatSetup();
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

    public void writeToTextArea(String encodedPublicKey, String message) {
        TextArea friendTextArea = this.friendsTextAreas.get(encodedPublicKey);
        if (friendTextArea == null) {
            return;
        }
        Friend friend = this.user.getFriend(encodedPublicKey);
        friendTextArea.appendText(friend.getUsername() + ": " + message + "\n");
    }

    private static class ActiveFriendTextArea {
        private TextArea textArea;
        private Friend friend;

        public void set(TextArea textArea, Friend friend) {
            this.textArea = textArea;
            this.friend = friend;
        }

        public Friend getFriend() {
            return friend;
        }

        public TextArea getTextArea() {
            return textArea;
        }
    }

    private static class CreateUserFields {
        public final TextField usernameField;
        public final PasswordField passwordField;

        public CreateUserFields(TextField usernameField, PasswordField passwordField) {
            this.usernameField = usernameField;
            this.passwordField = passwordField;
        }
    }
}
