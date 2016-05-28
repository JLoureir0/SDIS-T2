package pinypon.handler;

import pinypon.connection.ChatConnection;
import pinypon.protocol.ListeningThread;
import pinypon.protocol.chat.Protocol;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

final public class ChatHandler extends Thread implements ListeningThread {

    final private static long CONNECTIONS_COUNTER_START = Long.MIN_VALUE;
    private long connections_loop_counter = CONNECTIONS_COUNTER_START;

    final private LinkedBlockingQueue<ChatConnection> queuedConnections;
    final private HashMap<Long, Protocol> connectionsProtocols;
    private boolean interrupted = false;

    public ChatHandler() throws IOException {
        this.queuedConnections = new LinkedBlockingQueue<>();
        this.connectionsProtocols = new HashMap<>();
    }

    public void run() {
        try {
            while (!interrupted) {
                process_queued_connections();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
        connectionsProtocols.forEach((connectionId, protocol) -> {
            protocol.kill();
            try {
                protocol.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    private void process_queued_connections() throws IOException, InterruptedException {

        ChatConnection chatConnection = this.queuedConnections.take();
        while (chatConnection != null) {
            to_active_connection(chatConnection);
            chatConnection = this.queuedConnections.poll();
        }
    }

    private void to_active_connection(ChatConnection chatConnection) throws IOException, InterruptedException {

        Protocol protocol = new Protocol(chatConnection);
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
