package pinypon.handler.chat.ipeer;

import org.abstractj.kalium.crypto.Box;
import org.abstractj.kalium.crypto.Random;
import org.abstractj.kalium.encoders.Encoder;
import pinypon.connection.chat.ChatConnection;
import pinypon.interaction.gui.Gui;
import pinypon.protocol.ListeningThread;
import pinypon.protocol.chat.Message;
import pinypon.protocol.chat.ipeer.IPeerProtocol;
import pinypon.user.Friend;
import pinypon.user.User;
import pinypon.utils.Defaults;
import pinypon.utils.Tracker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.HashMap;

import static org.abstractj.kalium.NaCl.Sodium.NONCE_BYTES;

final public class IPeerHandler implements ListeningThread {

    final private Gui gui;
    final private HashMap<String, IPeerProtocol> establishedConnection;
    final private String trackerIp;
    final private int trackerPort;

    private boolean kill = false;

    public IPeerHandler(Gui gui, String trackerIp, int trackerPort) throws IOException {
        this.gui = gui;
        this.trackerIp = trackerIp;
        this.trackerPort = trackerPort;
        this.establishedConnection = new HashMap<>();
    }

    public synchronized boolean sendMessage(User user, Friend friend, int type, String message) {
        try {
            IPeerProtocol IPeerProtocol = establishedConnection.get(friend.getEncodedPublicKey());
            if (IPeerProtocol == null) {
                Tracker trackerReply = getFriendTrackerData(friend.getEncodedPublicKey());
                if (trackerReply == null) {
                    return false;
                }
                friend.setUsername(trackerReply.username);
                InetAddress ipAddress = InetAddress.getByName(trackerReply.ip);
                int port = Integer.parseInt(trackerReply.port);
                IPeerProtocol = new IPeerProtocol(user, friend, new ChatConnection(new Socket(ipAddress, port)), this.gui);
                IPeerProtocol.addListener(this);
                establishedConnection.put(friend.getEncodedPublicKey(), IPeerProtocol);
                IPeerProtocol.start();
            }
            Box cryptoBox = new Box(friend.getEncodedPublicKey(), user.getEncodedPrivateKey(), Encoder.HEX);
            byte[] nonce = new Random().randomBytes(NONCE_BYTES);
            String encodedNonce = Encoder.HEX.encode(nonce);
            if (message != null) {
                String cipheredText = Encoder.HEX.encode(cryptoBox.encrypt(nonce, message.getBytes()));
                IPeerProtocol.send(new Message(type, cipheredText, user.getEncodedPublicKey(), friend.getEncodedPublicKey(), encodedNonce));
            } else {
                IPeerProtocol.send(new Message(type, null, user.getEncodedPublicKey(), friend.getEncodedPublicKey(), encodedNonce));
            }

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

    public synchronized boolean sendMessage(User user, Friend friend, int type, String message, String username) {
        try {
            IPeerProtocol IPeerProtocol = establishedConnection.get(friend.getEncodedPublicKey());
            if (IPeerProtocol == null) {
                Tracker tracker = getFriendTrackerData(friend.getEncodedPublicKey());
                if (tracker == null) {
                    return false;
                }
                friend.setUsername(tracker.username);
                InetAddress ipAddress = InetAddress.getByName(tracker.ip);
                int port = Integer.parseInt(tracker.port);
                IPeerProtocol = new IPeerProtocol(user, friend, new ChatConnection(new Socket(ipAddress, port)), this.gui);
                IPeerProtocol.addListener(this);
                establishedConnection.put(friend.getEncodedPublicKey(), IPeerProtocol);
                IPeerProtocol.start();
            }

            Box cryptoBox = new Box(friend.getEncodedPublicKey(), user.getEncodedPrivateKey(), Encoder.HEX);
            byte[] nonce = new Random().randomBytes(NONCE_BYTES);
            String encodedNonce = Encoder.HEX.encode(nonce);
            if (message != null) {
                String cipheredText = Encoder.HEX.encode(cryptoBox.encrypt(nonce, message.getBytes()));
                IPeerProtocol.send(new Message(type, cipheredText, user.getEncodedPublicKey(), friend.getEncodedPublicKey(), encodedNonce, username));
            } else {
                IPeerProtocol.send(new Message(type, null, user.getEncodedPublicKey(), friend.getEncodedPublicKey(), encodedNonce, username));
            }

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

    private Tracker getFriendTrackerData(String friendEncodedPublicKey) {
        try {
            URL url = new URL("http://" + this.trackerIp + ":" + this.trackerPort + "/?id=" + friendEncodedPublicKey);
            URLConnection con = url.openConnection();
            HttpURLConnection http = (HttpURLConnection)con;
            http.setRequestMethod("GET");
            http.setRequestProperty("Content-Type", "application/json");
            http.connect();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(http.getInputStream()));
            String in = bufferedReader.readLine();
            Tracker tracker = Defaults.gson.fromJson(in, Tracker.class);
            System.out.println(tracker);
            return tracker;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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

