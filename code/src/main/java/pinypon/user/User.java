package pinypon.user;

import org.abstractj.kalium.keys.KeyPair;
import org.abstractj.kalium.keys.PrivateKey;
import pinypon.encryption.SymmetricEncryption;
import pinypon.utils.Defaults;
import pinypon.utils.ReadFile;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.HashSet;

public final class User extends Entity {

    private String password;
    private PrivateKey privateKey;
    private HashSet<Friend> friends;
    private String jsonPath;

    public User(String username, String password, String jsonPath) {
        super(username);
        if (password.isEmpty() || jsonPath.isEmpty()) {
            throw new IllegalArgumentException("Fields cannot be empty.");
        }
        KeyPair keyPair = new KeyPair();
        this.publicKey = keyPair.getPublicKey();
        this.privateKey = keyPair.getPrivateKey();
        this.password = password;
        this.jsonPath = jsonPath;
    }

    public String getJsonPath() {
        return jsonPath;
    }

    public void setPassword(String password) {
        this.password = password;
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

        byte[] encryptedJson = null;
        try {
            encryptedJson = SymmetricEncryption.encrypt(json, this.password);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(this.jsonPath, false);
            fileOutputStream.write(encryptedJson);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return true;
    }

    public static User restore(String path, String password) throws IOException, NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeySpecException {
        byte[] encryptedString = ReadFile.FileInputStreamBytes(path);
        String decryptedString = SymmetricEncryption.decrypt(encryptedString, password);
        return Defaults.gson.fromJson(decryptedString, User.class);
    }
}
