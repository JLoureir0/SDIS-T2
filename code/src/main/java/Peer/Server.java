package Peer;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;

final public class Server {
    private final int port;
    private final ArrayBlockingQueue<Connection> connectionsQueue;

    private Listener listener = null;
    private ConnectionsHandler connectionsHandler = null;

    public Server(int port, int connectionsQueueSize) {
        this.port = port;
        this.connectionsQueue = new ArrayBlockingQueue<>(connectionsQueueSize);
    }

    public void start() throws IOException {

        this.listener = new Listener(port, connectionsQueue);

        this.connectionsHandler = new ConnectionsHandler(connectionsQueue);

        new Thread(this.listener).start();
        new Thread(this.connectionsHandler).start();
    }
}