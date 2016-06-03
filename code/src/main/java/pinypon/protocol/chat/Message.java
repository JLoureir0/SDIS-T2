package pinypon.protocol.chat;

import java.io.Serializable;

public class Message implements Serializable {

    public static final int MESSAGE = 0;
    public static final int FRIEND_REQUEST = 1;
    public static final int ACCEPTED_FRIEND_REQUEST = 2;
    public static final int DENIED_FRIEND_REQUEST = 3;

    private int type;
    private String body;
    private String encodedSenderPublicKey;
    private String encodedReceiverPublicKey;
    private String encodedNonce;
    private String myUsername = null;

    public Message(int type, String body, String encodedSenderPublicKey, String encodedReceiverPublicKey, String encodedNonce) {
        this.type = type;
        this.body = body;
        this.encodedSenderPublicKey = encodedSenderPublicKey;
        this.encodedReceiverPublicKey = encodedReceiverPublicKey;
        this.encodedNonce = encodedNonce;
    }

    public Message(int type, String body, String encodedSenderPublicKey, String encodedReceiverPublicKey, String encodedNonce, String myUsername) {
        this.type = type;
        this.body = body;
        this.encodedSenderPublicKey = encodedSenderPublicKey;
        this.encodedReceiverPublicKey = encodedReceiverPublicKey;
        this.encodedNonce = encodedNonce;
        this.myUsername = myUsername;
    }

    public static int getMESSAGE() {
        return MESSAGE;
    }

    public String getNonce() {
        return encodedNonce;
    }

    public void setNonce(String nonce) {
        this.encodedNonce = nonce;
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

    public String getEncodedReceiverPublicKey() {
        return encodedReceiverPublicKey;
    }

    public void setEncodedReceiverPublicKey(String encodedReceiverPublicKey) {
        this.encodedReceiverPublicKey = encodedReceiverPublicKey;
    }
}
