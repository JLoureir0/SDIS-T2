package pinypon.handler.chat.ipeer;

import pinypon.connection.chat.ChatConnection;
import pinypon.protocol.ListeningThread;
import pinypon.protocol.chat.ipeer.Protocol;
import pinypon.user.Friend;
import pinypon.user.User;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;

final public class IPeerHandler implements ListeningThread {

    final private HashMap<String, Protocol> establishedConnection;

    private boolean kill = false;

    public IPeerHandler() throws IOException {
        this.establishedConnection = new HashMap<>();
    }

    public boolean sendMessage(User user, Friend friend, String message) throws IOException, InterruptedException {
        Protocol protocol = establishedConnection.get(friend.getEncodedPublicKey());
        if (protocol == null) {
            // TODO
            // public key, call a dht function that returns an ipaddress and a port of the listening peer
            String ipAddressString = "192.168.1.73";
            int port = 54321;
            InetAddress ipAddress = InetAddress.getByName(ipAddressString);
            // return false if is offline
            protocol = new Protocol(user, friend, new ChatConnection(new Socket(ipAddress, port)));
            establishedConnection.put(friend.getEncodedPublicKey(), protocol);
            protocol.start();
        }
        protocol.add(message);
        return true;
    }

    public void kill() {
        kill = true;
        establishedConnection.forEach((connectionId, protocol) -> {
            try {
                protocol.kill();
                protocol.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void notifyThreadComplete(Object object) {
        if (object == null) {
            return;
        }
        Protocol protocol = (Protocol) object;
        establishedConnection.remove(protocol.getChatConnection().getId());
    }
}

