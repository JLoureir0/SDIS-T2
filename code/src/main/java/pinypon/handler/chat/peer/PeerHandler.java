package pinypon.handler.chat.peer;

import pinypon.connection.chat.ChatConnection;
import pinypon.protocol.ListeningThread;
import pinypon.protocol.chat.Message;
import pinypon.protocol.chat.peer.Protocol;
import pinypon.user.User;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

final public class PeerHandler extends Thread implements ListeningThread {

    final private User user;
    final private static long CONNECTIONS_COUNTER_START = Long.MIN_VALUE;
    final private LinkedBlockingQueue<Message> messagesToPrint;
    final private LinkedBlockingQueue<ChatConnection> queuedConnections;
    final private HashMap<Long, Protocol> connectionsProtocols;
    private long connections_loop_counter = CONNECTIONS_COUNTER_START;
    private boolean interrupted = false;

    public PeerHandler(User user) throws IOException {
        this.messagesToPrint = new LinkedBlockingQueue<>();
        this.queuedConnections = new LinkedBlockingQueue<>();
        this.connectionsProtocols = new HashMap<>();
        this.user = user;
    }

    public void run() {
        try {
            process_queued_connections();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void process_queued_connections() throws IOException, InterruptedException {

        while (!interrupted) {
            ChatConnection chatConnection = this.queuedConnections.take();
            if (chatConnection == null) {
                throw new InterruptedException();
            }
            to_active_connection(chatConnection);

        }
    }

    private void to_active_connection(ChatConnection chatConnection) throws IOException, InterruptedException {

        Protocol protocol = new Protocol(this.user, chatConnection, this.messagesToPrint);
        protocol.addListener(this);

        do {
            ++this.connections_loop_counter;
            chatConnection.setId(this.connections_loop_counter);
            if (this.connectionsProtocols.putIfAbsent(this.connections_loop_counter, protocol) == null) {
                break;
            } else {
                Thread.currentThread().sleep(500);
            }
        } while (true);

        protocol.start();
    }

    public void put(ChatConnection chatConnection) {
        try {
            this.queuedConnections.put(chatConnection);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void kill() {
        interrupted = true;
        super.interrupt();
        try {
            this.messagesToPrint.put(null);
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

    public LinkedBlockingQueue<Message> getMessagesToPrint() {
        return messagesToPrint;
    }

    @Override
    public void notifyThreadComplete(Object object) {
        if (object == null) {
            return;
        }
        Protocol protocol = (Protocol) object;
        connectionsProtocols.remove(protocol.getChatConnection().getId());
    }
}
