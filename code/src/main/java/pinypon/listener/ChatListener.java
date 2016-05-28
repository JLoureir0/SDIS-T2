package pinypon.listener;

import pinypon.connection.ChatConnection;
import pinypon.handler.ChatHandler;

import java.io.IOException;
import java.net.*;

final public class ChatListener extends Thread {

    private final ServerSocket serverSocket;
    private final ChatHandler chatHandler;
    private boolean interrupted = false;

    public ChatListener(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.chatHandler = new ChatHandler();
        this.chatHandler.start();
    }

    public void run() {
        try {
            while (!interrupted) {
                Socket socket = this.serverSocket.accept();
                this.chatHandler.put(new ChatConnection(socket));
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void interrupt() {
        interrupted = true;
        super.interrupt();
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.chatHandler.put(null);
        this.chatHandler.interrupt();
        try {
            this.chatHandler.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

