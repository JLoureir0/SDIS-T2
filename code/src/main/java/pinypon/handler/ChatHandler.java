package pinypon.handler;

import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

final public class ChatHandler implements Runnable {

    final private LinkedBlockingQueue<Socket> queuedConnections;

    public ChatHandler() {
        this.queuedConnections = new LinkedBlockingQueue<>();
    }

    public void run() {
        while (true) {
            if(this.queuedConnections.isEmpty()) {
                Socket connection  = queuedConnections.poll();
                handleConnection(connection);
            }
        }
    }

    public void put(Socket socket) {
        try {
            this.queuedConnections.put(socket);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void handleConnection(Socket connection) {

    }
}
