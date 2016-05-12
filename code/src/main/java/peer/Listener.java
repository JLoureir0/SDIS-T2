package peer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.LinkedBlockingQueue;

final public class Listener implements Runnable {

    private int port;
    private ServerSocketChannel serverSocket;
    private LinkedBlockingQueue<Connection> queuedConnections;

    public Listener(int port, LinkedBlockingQueue<Connection> queuedConnections) {
        this.port = port;
        this.queuedConnections = queuedConnections;
    }

    public void run() {
        try {
            this.serverSocket = ServerSocketChannel.open();
            this.serverSocket.bind(new InetSocketAddress(port));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            while (true) {
                SocketChannel socketChannel = this.serverSocket.accept();
                this.queuedConnections.put(new Connection(socketChannel));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

