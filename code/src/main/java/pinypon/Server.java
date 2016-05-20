package pinypon;

import peer.Connection;
import pinypon.handler.DHTHandler;
import pinypon.listener.ChatListener;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

final public class Server {
    private final int port;

    private final LinkedBlockingQueue<Connection> queuedConnections;

    private ChatListener chatListener = null;

    private DHTHandler DHTHandler = null;

    public Server(int port) {
        this.port = port;
        this.queuedConnections = new LinkedBlockingQueue<>();
    }

    public void start() throws IOException {

        this.chatListener = new ChatListener(port, queuedConnections);

        this.DHTHandler = new DHTHandler(queuedConnections);

        new Thread(this.chatListener).start();
        new Thread(this.DHTHandler).start();
    }
}