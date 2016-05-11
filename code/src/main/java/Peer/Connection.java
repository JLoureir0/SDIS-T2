package Peer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

final public class Connection {

    final private SocketChannel socketChannel;

    private boolean alive = true;

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
}