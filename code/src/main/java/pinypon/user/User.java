package pinypon.user;

import pinypon.utils.Defaults;

import java.io.*;
import java.util.HashSet;

public class User extends Entity {

    private String privateKey;
    private HashSet<Friend> friends;
    private String jsonPath;

    public User(String username, String jsonPath, String publicKey, String privateKey) {
        super(username, publicKey);
        this.jsonPath = jsonPath;
        this.privateKey = privateKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public String getJsonPath() {
        return jsonPath;
    }

    public void setPrivateKey(String privateKey) {
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

    public static User restore(String path) {
        try {
            return Defaults.gson.fromJson(new FileReader(path), User.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
