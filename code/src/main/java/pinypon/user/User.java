package pinypon.user;

import org.abstractj.kalium.keys.PrivateKey;
import org.abstractj.kalium.keys.PublicKey;
import pinypon.utils.Defaults;

import java.io.*;
import java.util.HashSet;

public class User extends Entity {

    private String password;
    private PrivateKey privateKey;
    private HashSet<Friend> friends;
    private String jsonPath;

    public User(String username, String password, String jsonPath, PublicKey publicKey, PrivateKey privateKey) {
        super(username, publicKey);
        if (password.isEmpty() || jsonPath.isEmpty() || privateKey == null) {
            throw new IllegalArgumentException("Fields cannot be empty.");
        }
        this.password = password;
        this.jsonPath = jsonPath;
        this.privateKey = privateKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public String getJsonPath() {
        return jsonPath;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public void setJsonPath(String jsonPath) {
        this.jsonPath = jsonPath;
    }

    public boolean addFriend(Friend friend) {
        return this.friends.add(friend);
    }

    public boolean removeFriend(Friend friend) {
        return this.friends.remove(friend);
    }

    public boolean store() {
        if (this.jsonPath == null) {
            return false;
        }
        String json = Defaults.gson.toJson(this, User.class);
        if (json.isEmpty()) {
            return false;
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(this.jsonPath));
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static User restore(String path) throws FileNotFoundException {
        return Defaults.gson.fromJson(new FileReader(path), User.class);
    }
}
