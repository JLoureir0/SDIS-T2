package pinypon.handler.chat.ipeer;

import pinypon.connection.chat.ChatConnection;
import pinypon.interaction.gui.Gui;
import pinypon.protocol.ListeningThread;
import pinypon.protocol.chat.Message;
import pinypon.protocol.chat.ipeer.IPeerProtocol;
import pinypon.user.Friend;
import pinypon.user.User;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

final public class IPeerHandler implements ListeningThread {

    final private Gui gui;
    final private HashMap<String, IPeerProtocol> establishedConnection;

    private boolean kill = false;

    public IPeerHandler(Gui gui) throws IOException {
        this.gui = gui;
        this.establishedConnection = new HashMap<>();
    }

    public synchronized boolean sendMessage(User user, Friend friend, int type, String message) {
        try {
            IPeerProtocol IPeerProtocol = establishedConnection.get(friend.getEncodedPublicKey());
            if (IPeerProtocol == null) {
                // TODO
                // public key, call a dht function that returns an ipAddress and a port of the listening peer
                String ipAddressString = "127.0.0.1";
                int port = 44321;
                InetAddress ipAddress = InetAddress.getByName(ipAddressString);
                // return false if is offline
                IPeerProtocol = new IPeerProtocol(user, friend, new ChatConnection(new Socket(ipAddress, port)), this.gui);
                IPeerProtocol.addListener(this);
                establishedConnection.put(friend.getEncodedPublicKey(), IPeerProtocol);
                IPeerProtocol.start();
            }
            IPeerProtocol.send(new Message(type, message, friend.getEncodedPublicKey()));
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public synchronized void kill() {
        kill = true;
        establishedConnection.forEach((connectionId, protocol) -> {
            protocol.kill();
            try {
                protocol.join();
            } catch (InterruptedException e) {
            }
        });
    }

    @Override
    public synchronized void notifyThreadComplete(Object object) {
        if (object == null) {
            return;
        }
        IPeerProtocol IPeerProtocol = (IPeerProtocol) object;
        if (establishedConnection.remove(IPeerProtocol.getFriend().getEncodedPublicKey()) == null) {
            throw new IllegalStateException("Expecting IPeerProtocol removal");
        }
        IPeerProtocol.kill();
        try {
            IPeerProtocol.join();
        } catch (InterruptedException e) {
        }
    }
}

