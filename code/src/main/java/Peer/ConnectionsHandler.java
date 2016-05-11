package Peer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

final public class ConnectionsHandler implements Runnable {

    final private static long CONNECTIONS_COUNTER_START = Long.MIN_VALUE;
    final private static int BUFFER_SIZE = 1024 * 1024;

    final private ArrayBlockingQueue<Connection> connectionsQueue;

    final private HashMap<Long, ConnectionMessages> connectionsMessages = new HashMap<>();
    final private HashSet<ConnectionMessages> outboundConnectionMessages = new HashSet<>();

    final private ByteBuffer interimReadBuffer = ByteBuffer.allocate(BUFFER_SIZE);
    final private ByteBuffer interimWriteBuffer = ByteBuffer.allocate(BUFFER_SIZE);

    final private Selector readSelector;
    final private Selector writeSelector;

    private long connections_loop_counter = CONNECTIONS_COUNTER_START;

    public ConnectionsHandler(ArrayBlockingQueue<Connection> connectionsQueue) throws IOException {
        this.connectionsQueue = connectionsQueue;
        this.readSelector = Selector.open();
        this.writeSelector = Selector.open();
    }

    public void run() {
        while (true) {
            try {
                process_queued_connections();
                check_for_inbound_messages();
                check_for_outbound_messages();
                Thread.sleep(100);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void process_queued_connections() throws IOException {

        Connection connection = this.connectionsQueue.poll();

        while (connection != null) {
            this.create_connectionMessages(connection);
            connection = this.connectionsQueue.poll();
        }
    }

    private void create_connectionMessages(Connection connection) throws IOException {

        do {
            ++this.connections_loop_counter;
        } while (this.connectionsMessages.containsKey(this.connections_loop_counter));

        ConnectionMessages connectionMessages = new ConnectionMessages(this.connections_loop_counter, connection);
        this.connectionsMessages.putIfAbsent(this.connections_loop_counter, connectionMessages);

        connection.setBlocking(false);
        SelectionKey key = connection.register(this.readSelector, SelectionKey.OP_READ);
        key.attach(connectionMessages);
    }

    public void check_for_inbound_messages() throws IOException {
        int readReady = this.readSelector.selectNow();

        if (readReady > 0) {
            Set<SelectionKey> selectedKeys = this.readSelector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

            while (keyIterator.hasNext()) {
                SelectionKey selectionKey = keyIterator.next();
                process_inbound_messages(selectionKey);
                keyIterator.remove();
            }
            selectedKeys.clear();
        }
    }

    private void process_inbound_messages(SelectionKey key) throws IOException {
        ConnectionMessages connectionMessages = (ConnectionMessages) key.attachment();

        boolean connection_alive = connectionMessages.read(this.interimReadBuffer);

        if (Protocol.transition(connectionMessages) > 0) {
            this.outboundConnectionMessages.add(connectionMessages);
        }

        if (!connection_alive) {
            this.connectionsMessages.remove(connectionMessages.getId());
            this.outboundConnectionMessages.remove(connectionMessages.getId());
            key.attach(null);
            key.cancel();
            key.channel().close();
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
