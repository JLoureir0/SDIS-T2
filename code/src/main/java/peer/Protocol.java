package peer;

import peer.actions.Action;
import peer.actions.ActionOnFileReceive;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

final public class Protocol {
    public enum STATE {
        START,
        ACK,
        SOMESTATE
    }

    final private ConcurrentHashMap<Long, Connection> activeConnections;

    public Protocol(ConcurrentHashMap<Long, Connection> activeConnections) {
        this.activeConnections = activeConnections;
    }

    public Action transition(Message inboundMessage) {

        long connectionId = inboundMessage.getConnectionId();
        Connection connection = this.activeConnections.get(connectionId);
        if (connection == null) {
            throw new IllegalStateException("Connection should be present in activeConnections");
        }

        if (connection.isLockedToAction()) {
            throw new IllegalStateException("Connection should only receive messages after an action has ended");
        }

        STATE connectionState = connection.getState();
        Message.TYPE messageType = inboundMessage.getType();

        switch (connectionState) {
            case START:
                switch (messageType) {
                    case FILE_RECEIVE:
                        connection.setState(STATE.START);
                        try {
                            return new ActionOnFileReceive(connection, "path/where/to/save/file");
                        } catch (IOException e) {
                            e.printStackTrace();
                            return null;
                        }
                }
                break;
            default:
                throw new IllegalStateException("Unknown State");
        }
        return null;
    }
}
