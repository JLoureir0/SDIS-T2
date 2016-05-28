package pinypon.protocol.chat;

import org.abstractj.kalium.keys.PublicKey;

import java.io.Serializable;

public class Message implements Serializable {

    public static final int MESSAGE = 0;
    private PublicKey senderPublicKey;
    private int type;
    private String body;

    public Message(int type, String body, PublicKey senderPublicKey) {
        this.type = type;
        this.body = body;
        this.senderPublicKey = senderPublicKey;
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

    public PublicKey getSenderPublicKey() {
        return senderPublicKey;
    }

    public void setSenderPublicKey(PublicKey senderPublicKey) {
        this.senderPublicKey = senderPublicKey;
    }
}
