package pinypon.listener;

import pinypon.connection.chat.ChatConnection;
import pinypon.handler.chat.peer.PeerHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

final public class ChatListener extends Thread {

    private final ServerSocket serverSocket;
    private final PeerHandler peerHandler;
    private boolean kill = false;

    public ChatListener(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.peerHandler = new PeerHandler();
        this.peerHandler.start();
    }

    public PeerHandler getPeerHandler() {
        return peerHandler;
    }

    public void run() {
        try {
            while (!kill) {
                Socket socket = this.serverSocket.accept();
                this.peerHandler.put(new ChatConnection(socket));
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void kill() {
        kill = true;
        super.interrupt();
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.peerHandler.kill();
        try {
            this.peerHandler.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

