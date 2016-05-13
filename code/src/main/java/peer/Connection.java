package peer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Comparator;

final public class Connection implements Comparator<Connection> {

    private Protocol.STATE state = Protocol.STATE.START;
    final private SocketChannel socketChannel;
    private boolean lockedToAction = false;
    private boolean alive = true;
    private long id;

    public Connection(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    public int read(ByteBuffer byteBuffer) throws IOException, IllegalStateException {

        int totalBytesRead = 0;
        int bytesRead;

        do {
            bytesRead = this.socketChannel.read(byteBuffer);
            if (bytesRead == -1) {
                this.alive = false;
                break;
            }
            totalBytesRead += bytesRead;
        } while (bytesRead > 0);

        return totalBytesRead;
    }

    public int write(ByteBuffer byteBuffer) throws IOException {

        int bytesWritten;
        int totalBytesWritten = 0;

        do {
            bytesWritten = this.socketChannel.write(byteBuffer);
            totalBytesWritten += bytesWritten;
        } while (bytesWritten > 0 && byteBuffer.hasRemaining());

        return totalBytesWritten;
    }

    public boolean is_alive() {
        return this.alive;
    }

    public void setBlocking(boolean blocking) throws IOException {
        this.socketChannel.configureBlocking(blocking);
    }

    public SelectionKey register(Selector selector, int operation) throws ClosedChannelException {
        return this.socketChannel.register(selector, operation);
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public Protocol.STATE getState() {
        return state;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(this.id);
    }

    @Override
    public int compare(Connection connection, Connection otherConnection) {
        return Long.compare(connection.id, otherConnection.id);
    }

    @Override
    public boolean equals(Object obj) {
        Connection otherConnection = (Connection) obj;
        return otherConnection.id == this.id;
    }

    public boolean isLockedToAction() {
        return this.lockedToAction;
    }

    public void setLockedToAction(boolean lockedToAction) {
        this.lockedToAction = lockedToAction;
    }

    public void setState(Protocol.STATE state) {
        this.state = state;
    }
}