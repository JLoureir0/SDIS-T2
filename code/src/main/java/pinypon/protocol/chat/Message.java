package pinypon.protocol.chat;

import java.io.Serializable;

public class Message implements Serializable {

    public static final int MESSAGE = 0;
    private String encodedSenderPublicKey;
    private int type;
    private String body;

    public Message(int type, String body, String encodedSenderPublicKey) {
        this.type = type;
        this.body = body;
        this.encodedSenderPublicKey = encodedSenderPublicKey;
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

    public String getEncodedSenderPublicKey() {
        return encodedSenderPublicKey;
    }

    public void setEncodedSenderPublicKey(String encodedSenderPublicKey) {
        this.encodedSenderPublicKey = encodedSenderPublicKey;
    }
}