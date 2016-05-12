package peer;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

final public class Server {
    private final int port;

    private final LinkedBlockingQueue<Connection> queuedConnections;

    private Listener listener = null;

    private ConnectionsHandler connectionsHandler = null;

    public Server(int port, int queuedConnectionsSize) {
        this.port = port;
        this.queuedConnections = new LinkedBlockingQueue<>(queuedConnectionsSize);
    }

    public void start() throws IOException {

        this.listener = new Listener(port, queuedConnections);

        this.connectionsHandler = new ConnectionsHandler(queuedConnections);

        new Thread(this.listener).start();
        new Thread(this.connectionsHandler).start();
    }
}