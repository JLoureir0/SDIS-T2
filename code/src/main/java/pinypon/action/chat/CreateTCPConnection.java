package pinypon.action.chat;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class CreateTCPConnection {

    public static Socket create(InetAddress address, int port) throws IOException {
        return new Socket(address, port);
    }
}
