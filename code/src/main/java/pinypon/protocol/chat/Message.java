package pinypon.protocol.chat;

import java.io.Serializable;
import org.abstractj.kalium.keys.PublicKey;

public class Message implements Serializable {

    private PublicKey senderPublicKey;
    public static final int MESSAGE = 0;
    private int type;
    private String body;

    public Message(int type, String body, PublicKey senderPublicKey) {
        this.type = type;
        this.body = body;
        this.senderPublicKey = senderPublicKey;
    }

    public int getType() {
        return type;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setType(int type) {
        this.type = type;
    }

    public static int getMESSAGE() {
        return MESSAGE;
    }

    public PublicKey getSenderPublicKey() {
        return senderPublicKey;
    }

    public void setSenderPublicKey(PublicKey senderPublicKey) {
        this.senderPublicKey = senderPublicKey;
    }
}
