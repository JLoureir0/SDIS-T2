package pinypon.listener;

import pinypon.handler.ChatHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

final public class ChatListener implements Runnable {

    private int port;
    private ServerSocket serverSocket;
    private ChatHandler chatHandler;

    public ChatListener(int port) {
        this.port = port;
        this.chatHandler = new ChatHandler();
        this.chatHandler.run();
    }

    public void run() {
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            while (true) {
                Socket socket = this.serverSocket.accept();
                this.chatHandler.put(socket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

