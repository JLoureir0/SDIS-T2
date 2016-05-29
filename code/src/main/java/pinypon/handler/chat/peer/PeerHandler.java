package pinypon.handler.chat.peer;

import pinypon.connection.chat.ChatConnection;
import pinypon.interaction.gui.Gui;
import pinypon.protocol.ListeningThread;
import pinypon.protocol.chat.Message;
import pinypon.protocol.chat.peer.PeerProtocol;
import pinypon.user.Friend;
import pinypon.user.User;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

final public class PeerHandler extends Thread implements ListeningThread {

    final private Gui gui;
    final private static long CONNECTIONS_COUNTER_START = Long.MIN_VALUE;
    final private User user;
    final private LinkedBlockingQueue<ChatConnection> queuedConnections;
    final private HashMap<Long, PeerProtocol> connectionsProtocols;
    private long connections_loop_counter = CONNECTIONS_COUNTER_START;
    private boolean interrupted = false;

    public PeerHandler(User user, Gui gui) throws IOException {
        this.queuedConnections = new LinkedBlockingQueue<>();
        this.connectionsProtocols = new HashMap<>();
        this.user = user;
        this.gui = gui;
    }

    public void run() {
        try {
            process_queued_connections();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
        }
    }

    private void process_queued_connections() throws IOException, InterruptedException {

        while (!interrupted) {
            ChatConnection chatConnection = this.queuedConnections.take();
            if (chatConnection == null) {
                return;
            }
            to_active_connection(chatConnection);
        }
    }

    private void to_active_connection(ChatConnection chatConnection) throws IOException, InterruptedException {

        PeerProtocol peerProtocol = new PeerProtocol(this.user, chatConnection, gui);
        peerProtocol.addListener(this);

        do {
            ++this.connections_loop_counter;
            chatConnection.setId(this.connections_loop_counter);
            if (this.connectionsProtocols.putIfAbsent(this.connections_loop_counter, peerProtocol) == null) {
                break;
            } else {
                Thread.currentThread().sleep(500);
            }
        } while (true);

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
        connectionsProtocols.remove(peerProtocol.getChatConnection().getId());
    }

    public synchronized boolean sendMessage(User user, Friend friend, int type, String message) {
        try {
            PeerProtocol peerProtocol = connectionsProtocols.get(friend.getEncodedPublicKey());
            if (peerProtocol == null) {
                return false;
            }
            peerProtocol.send(new Message(type, message, friend.getEncodedPublicKey()));
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
                return false;
            }
            peerProtocol.send(new Message(type, message, friendEncodedPublicKey));
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
}
