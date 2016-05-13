package peer;

import peer.actions.Action;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

final public class ConnectionsHandler implements Runnable {

    final private ConnectionsReader connectionsReader;
    final private ConnectionsWriter connectionsWriter;
    final private Protocol protocol;

    public ConnectionsHandler(LinkedBlockingQueue<Connection> queuedConnections) throws IOException {
        this.connectionsReader = new ConnectionsReader(queuedConnections);
        this.connectionsWriter = new ConnectionsWriter(this.connectionsReader.getActiveConnections());

        new Thread(this.connectionsReader).start();
        new Thread(this.connectionsWriter).start();
        this.protocol = new Protocol(this.connectionsReader.getActiveConnections());
    }

    public void run() {
        LinkedBlockingQueue<Message> inboundMessages = this.connectionsReader.getInboundMessages();
        while (true) {
            try {
                Action action = protocol.transition(inboundMessages.take());
                this.connectionsWriter.put(action);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
