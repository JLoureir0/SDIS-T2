package pinypon.handler;

import peer.Connection;
import peer.ConnectionsAction;
import peer.ConnectionsReader;
import peer.Message;
import peer.actions.Action;
import pinypon.protocol.chat.Protocol;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.util.concurrent.LinkedBlockingQueue;

final public class DHTHandler implements Runnable {

    final private ConnectionsReader connectionsReader;
    final private ConnectionsAction connectionsAction;
    final private Protocol protocol;

    public DHTHandler(LinkedBlockingQueue<Connection> queuedConnections) throws IOException {
        this.connectionsReader = new ConnectionsReader(queuedConnections);
        this.protocol = new Protocol(this.connectionsReader.getActiveConnections());
        this.connectionsAction = new ConnectionsAction();

        new Thread(this.connectionsReader).start();
        new Thread(this.connectionsAction).start();
    }

    public void run() {
        LinkedBlockingQueue<Message> inboundMessages = this.connectionsReader.getInboundMessages();
        while (true) {
            try {
                Action action = protocol.transition(inboundMessages.take());
                this.connectionsAction.put(action);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ClosedChannelException e) {
                e.printStackTrace();
            }
        }
    }
}
