package pinypon.listener;

import pinypon.connection.chat.ChatConnection;
import pinypon.handler.chat.peer.PeerHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

final public class ChatListener extends Thread {

    private final ServerSocket serverSocket;
    private final PeerHandler chatHandler;
    private boolean interrupted = false;

    public ChatListener(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.chatHandler = new PeerHandler();
        this.chatHandler.start();
    }

    public PeerHandler getChatHandler() {
        return chatHandler;
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

