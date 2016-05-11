package Peer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ArrayBlockingQueue;

final public class Listener implements Runnable {

    private int port;
    private ServerSocketChannel serverSocket;
    private ArrayBlockingQueue<Connection> connectionsQueue;

    public Listener(int port, ArrayBlockingQueue<Connection> ConnectionsQueue) {
        this.port = port;
        this.connectionsQueue = ConnectionsQueue;
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
                this.connectionsQueue.put(new Connection(socketChannel));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

