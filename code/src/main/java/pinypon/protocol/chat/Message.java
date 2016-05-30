package pinypon.protocol.chat;

import java.io.Serializable;

public class Message implements Serializable {

    public static final int MESSAGE = 0;
    public static final int END_MESSAGE = 1;
    public static final int FRIEND_REQUEST = 2;
    public static final int ACCEPT_FRIEND_REQUEST = 3;
    public static final int DENY_FRIEND_REQUEST = 4;

    private String encodedSenderPublicKey;
    private int type;
    private String body;
    private String myUsername = null;

    public Message(int type, String body, String encodedSenderPublicKey) {
        this.type = type;
        this.body = body;
        this.encodedSenderPublicKey = encodedSenderPublicKey;
    }

    public Message(int type, String body, String encodedSenderPublicKey, String myUsername) {
        this.type = type;
        this.body = body;
        this.encodedSenderPublicKey = encodedSenderPublicKey;
        this.myUsername = myUsername;
    }

    public static int getMESSAGE() {
        return MESSAGE;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getMyUsername() {
        return myUsername;
    }

    public String getEncodedSenderPublicKey() {
        return encodedSenderPublicKey;
    }

    public void setEncodedSenderPublicKey(String encodedSenderPublicKey) {
        this.encodedSenderPublicKey = encodedSenderPublicKey;
    }
}
