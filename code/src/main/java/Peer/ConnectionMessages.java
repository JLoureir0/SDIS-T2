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

    private WannabeMessage wannabeMessage = new WannabeMessage();

    ConnectionMessages(long id, Connection connection) {
        this.id = id;
        this.connection = connection;
    }

    public boolean read(ByteBuffer interimByteBuffer) throws IOException {

        this.connection.read(interimByteBuffer);
        interimByteBuffer.flip();

        if (interimByteBuffer.remaining() == 0) {
            interimByteBuffer.clear();
            return this.connection.is_alive();
        }

        this.nextMessage.writeToMessage(interimByteBuffer);

        int endIndex = HttpUtil.parseHttpRequest(this.nextMessage.sharedArray, this.nextMessage.offset, this.nextMessage.offset + this.nextMessage.length, (HttpHeaders) this.nextMessage.metaData);
        if (endIndex != -1) {
            Message message = this.messageBuffer.getMessage();
            message.metaData = new HttpHeaders();

            message.writePartialMessageToMessage(nextMessage, endIndex);

            completeMessages.add(nextMessage);
            nextMessage = message;
        }
        interimByteBuffer.clear();
    }

    public long getId() {
        return this.id;
    }

    public Connection getConnection() {
        return this.connection;
    }

    public LinkedList<Message> getInboundMessages() {
        return inboundMessages;
    }

    public LinkedList<MessageAction> getOutboundMessages() {
        return this.outboundMessages;
    }

    public Protocol.STATE getFiniteAutomatonState() {
        return this.finiteAutomatonState;
    }

    public void setFiniteAutomatonState(Protocol.STATE finiteAutomatonState) {
        this.finiteAutomatonState = finiteAutomatonState;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(this.id);
    }

    @Override
    public boolean equals(Object obj) {
        ConnectionMessages otherConnectionMessages = (ConnectionMessages) obj;
        return otherConnectionMessages.id == this.id;
    }
}
