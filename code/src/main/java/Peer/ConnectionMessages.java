package Peer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedList;

final public class ConnectionMessages {

    final private long id;
    final private Connection connection;

    private Protocol.STATE finiteAutomatonState = Protocol.STATE.START;

    private LinkedList<Message> inboundMessages = new LinkedList<>();
    private LinkedList<MessageAction> outboundMessages = new LinkedList<>();

    ConnectionMessages(long id, Connection connection) {
        this.id = id;
        this.connection = connection;
    }
}
