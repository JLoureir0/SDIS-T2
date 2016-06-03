package pinypon.handler.chat.peer;

import org.abstractj.kalium.crypto.Box;
import org.abstractj.kalium.crypto.Random;
import org.abstractj.kalium.encoders.Encoder;
import pinypon.connection.chat.ChatConnection;
import pinypon.interaction.gui.Gui;
import pinypon.protocol.ListeningThread;
import pinypon.protocol.chat.Message;
import pinypon.protocol.chat.peer.PeerProtocol;
import pinypon.user.Friend;
import pinypon.user.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

import static org.abstractj.kalium.NaCl.Sodium.NONCE_BYTES;

final public class PeerHandler extends Thread implements ListeningThread {

    final private Gui gui;
    final private User user;
    final private LinkedBlockingQueue<ChatConnection> queuedConnections;
    final private HashMap<String, PeerProtocol> connectionsProtocols;
    private boolean interrupted = false;

    public PeerHandler(User user, Gui gui) throws IOException {
        this.queuedConnections = new LinkedBlockingQueue<>();
        this.connectionsProtocols = new HashMap<>();
        this.user = user;
        this.gui = gui;
    }

    public void run() {
        try {
            try {
                process_queued_connections();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
        }
    }

    private void process_queued_connections() throws IOException, InterruptedException, ClassNotFoundException {

        while (!interrupted) {
            ChatConnection chatConnection = this.queuedConnections.take();
            if (chatConnection == null) {
                return;
            }
            to_active_connection(chatConnection);
        }
    }

    private void to_active_connection(ChatConnection chatConnection) throws IOException, InterruptedException, ClassNotFoundException {

        PeerProtocol peerProtocol = new PeerProtocol(this.user, chatConnection, gui, this);

        peerProtocol.addListener(this);
        peerProtocol.start();
    }

    public synchronized void put(ChatConnection chatConnection) {
        try {
            this.queuedConnections.put(chatConnection);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized void kill() {
        interrupted = true;
        super.interrupt();
        try {
            this.queuedConnections.put(null);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.connectionsProtocols.forEach((connectionId, protocol) -> {
            protocol.kill();
            try {
                protocol.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public synchronized void notifyThreadComplete(Object object) {
        if (object == null) {
            return;
        }
        PeerProtocol peerProtocol = (PeerProtocol) object;
        peerProtocol.kill();
        connectionsProtocols.remove(peerProtocol.getEncodedFriendPublicKey());
    }

    public synchronized boolean sendMessage(User user, Friend friend, int type, String message) {
        try {
            PeerProtocol peerProtocol = connectionsProtocols.get(friend.getEncodedPublicKey());
            if (peerProtocol == null) {
                return false;
            }
                Box cryptoBox = new Box(friend.getEncodedPublicKey(), user.getEncodedPrivateKey(), Encoder.HEX);
                byte[] nonce = new Random().randomBytes(NONCE_BYTES);
                String encodedNonce = Encoder.HEX.encode(nonce);
            if (message != null) {
                String cipheredText = Encoder.HEX.encode(cryptoBox.encrypt(nonce, message.getBytes()));
                peerProtocol.send(new Message(type, cipheredText, user.getEncodedPublicKey(), friend.getEncodedPublicKey(), encodedNonce));
            } else {
                peerProtocol.send(new Message(type, null, user.getEncodedPublicKey(), friend.getEncodedPublicKey(), encodedNonce));
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

    public synchronized boolean sendMessage(User user, String friendEncodedPublicKey, int type, String message) {
        try {
            PeerProtocol peerProtocol = connectionsProtocols.get(friendEncodedPublicKey);
            if (peerProtocol == null) {
                System.out.println("NULL peer proto");
                return false;
            }
            Box cryptoBox = new Box(friendEncodedPublicKey, user.getEncodedPrivateKey(), Encoder.HEX);
            byte[] nonce = new Random().randomBytes(NONCE_BYTES);
            String encodedNonce = Encoder.HEX.encode(nonce);
            if (message != null) {
                String cipheredText = Encoder.HEX.encode(cryptoBox.encrypt(nonce, message.getBytes()));
                peerProtocol.send(new Message(type, cipheredText, user.getEncodedPublicKey(), friendEncodedPublicKey, encodedNonce));
            } else {
                peerProtocol.send(new Message(type, null, user.getEncodedPublicKey(), friendEncodedPublicKey, encodedNonce));
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

    public void addConnection(String key, PeerProtocol peerProtocol) {
        connectionsProtocols.put(key, peerProtocol);
    }
}
