package peer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class ConnectionsReader implements Runnable {

    final private static long CONNECTIONS_COUNTER_START = Long.MIN_VALUE;
    private long connections_loop_counter = CONNECTIONS_COUNTER_START;

    final private LinkedBlockingQueue<Connection> connectionsQueue;
    final private ConcurrentHashMap<Long, Connection> activeConnections;
    final private LinkedBlockingQueue<Message> inboundMessages;

    final private Selector readSelector;
    final private ByteBuffer interimReadBuffer;

    public ConnectionsReader(LinkedBlockingQueue<Connection> queuedConnections) throws IOException {
        this.connectionsQueue = queuedConnections;
        this.activeConnections = new ConcurrentHashMap<>();
        this.inboundMessages = new LinkedBlockingQueue<>();
        this.readSelector = Selector.open();
        this.interimReadBuffer = ByteBuffer.allocateDirect(Constants.BYTE_BUFFER_SIZE);
    }

    @Override
    public void run() {
        while (true) {
            try {
                process_queued_connections();
                check_for_connection_data();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void process_queued_connections() throws IOException {

        Connection connection = this.connectionsQueue.poll();

        while (connection != null) {
            this.to_active_connection(connection);
            connection = this.connectionsQueue.poll();
        }
    }

    private void to_active_connection(Connection connection) throws IOException {

        do {
            ++this.connections_loop_counter;
            connection.setId(this.connections_loop_counter);
        } while (this.activeConnections.putIfAbsent(this.connections_loop_counter, connection) != null);

        connection.setBlocking(false);
        SelectionKey key = connection.register(this.readSelector, SelectionKey.OP_READ);
        key.attach(connection);
    }

    private void check_for_connection_data() throws IOException {
        int readReady = this.readSelector.selectNow();

        if (readReady > 0) {
            Set<SelectionKey> selectedKeys = this.readSelector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

            while (keyIterator.hasNext()) {
                SelectionKey selectionKey = keyIterator.next();
                Connection connection = (Connection) selectionKey.attachment();
                build_message(connection);
                keyIterator.remove();
            }
            selectedKeys.clear();
        }
    }

    private void build_message(Connection connection) throws IOException {
        // Try to build a message using a buffer and a function to parse the buffer contents
        // If the function builds a valid message create a new Message and then erase the buffer.
        // Then add it to the inboundMessages.
        // Each connection has to have a buffer for a partial message...
        // And there must exist a static function that checks to see if the message is valid!
        // Check headers and other stuff

        connection.read(interimReadBuffer);
        interimReadBuffer.flip();

        if (interimReadBuffer.remaining() == 0) {
            interimReadBuffer.clear();
            return;
        }

        connection.partialMessage.writeToMessage(interimReadBuffer);

        int endIndex = HttpUtil.parseHttpRequest(this.nextMessage.sharedArray, this.nextMessage.offset, this.nextMessage.offset + this.nextMessage.length, (HttpHeaders) this.nextMessage.metaData);
        if (endIndex != -1) {
            Message message = this.messageBuffer.getMessage();
            message.metaData = new HttpHeaders();

            message.writePartialMessageToMessage(nextMessage, endIndex);

            completeMessages.add(nextMessage);
            nextMessage = message;
        }
        interimReadBuffer.clear();
    }

    public ConcurrentHashMap<Long, Connection> getActiveConnections() {
        return activeConnections;
    }

    public LinkedBlockingQueue<Message> getInboundMessages() {
        return inboundMessages;
    }
}
