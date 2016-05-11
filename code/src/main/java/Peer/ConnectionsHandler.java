package Peer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

final public class ConnectionsHandler implements Runnable {

    final private static int BUFFER_SIZE = 1024 * 1024;

    final private ConnectionsReader connectionsReader;
    final private ConnectionsWriter connectionsWriter;

    public ConnectionsHandler(LinkedBlockingQueue<Connection> queuedConnections) throws IOException {

        this.connectionsReader = new ConnectionsReader(queuedConnections, BUFFER_SIZE);
        this.connectionsWriter = new ConnectionsWriter(this.connectionsReader.getActiveConnections(), BUFFER_SIZE);

        new Thread(this.connectionsReader).start();
        new Thread(this.connectionsWriter).start();

//        this.writeSelector = Selector.open();
    }

    public void run() {
        while (true) {
            LinkedBlockingQueue<Message> inboundMessages = this.connectionsReader.getInboundMessages();
        }
    }

    private void check_for_outbound_messages() throws IOException {

        // Take all new messages from outboundMessageQueue
        takeNewOutboundMessages();

        // Cancel all sockets which have no more data to write.
        cancelEmptySockets();

        // Register all sockets that *have* data and which are not yet registered.
        registerNonEmptySockets();

        // Select from the Selector.
        int writeReady = this.writeSelector.selectNow();

        if (writeReady > 0) {
            Set<SelectionKey> selectionKeys = this.writeSelector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectionKeys.iterator();

            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();

                Connection socket = (Connection) key.attachment();

                socket.messageWriter.write(socket, this.interimWriteBuffer);

                if (socket.messageWriter.isEmpty()) {
                    this.nonEmptyToEmptySockets.add(socket);
                }

                keyIterator.remove();
            }

            selectionKeys.clear();

        }
    }

    private void takeNewOutboundMessages() {
        Message outMessage = this.outboundMessageQueue.poll();
        while (outMessage != null) {
            Connection socket = this.connectionsMessages.get(outMessage.socketId);

            if (socket != null) {
                MessageWriter messageWriter = socket.messageWriter;
                if (messageWriter.isEmpty()) {
                    messageWriter.enqueue(outMessage);
                    nonEmptyToEmptySockets.remove(socket);
                    emptyToNonEmptySockets.add(socket);    //not necessary if removed from nonEmptyToEmptySockets in prev. statement.
                } else {
                    messageWriter.enqueue(outMessage);
                }
            }

            outMessage = this.outboundMessageQueue.poll();
        }
    }

    private void registerNonEmptySockets() throws ClosedChannelException {
        for (Connection socket : emptyToNonEmptySockets) {
            socket.socketChannel.register(this.writeSelector, SelectionKey.OP_WRITE, socket);
        }
        emptyToNonEmptySockets.clear();
    }

    private void cancelEmptySockets() {
        for (Connection socket : nonEmptyToEmptySockets) {
            SelectionKey key = socket.socketChannel.keyFor(this.writeSelector);

            key.cancel();
        }
        nonEmptyToEmptySockets.clear();
    }

}
