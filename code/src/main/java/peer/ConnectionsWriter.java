package peer;

import peer.actions.Action;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class ConnectionsWriter implements Runnable {

    final private ConcurrentHashMap<Long, Connection> activeConnections;
    final private LinkedBlockingQueue<Action> getActions;

    final private Selector writeSelector;
    final private ByteBuffer interimWriteBuffer;


    public ConnectionsWriter(ConcurrentHashMap<Long, Connection> activeConnections) throws IOException {
        this.activeConnections = activeConnections;
        this.getActions = new LinkedBlockingQueue<>();
        this.writeSelector = Selector.open();
        this.interimWriteBuffer = ByteBuffer.allocateDirect(Constants.BYTE_BUFFER_SIZE);
    }

    @Override
    public void run() {
        Iterator<Action> actionIterator = getActions.iterator();
        while (actionIterator.hasNext()) {
            Action action = actionIterator.next();
            if (action.execute(interimWriteBuffer)) {
                actionIterator.remove();
            }
        }
    }

    public LinkedBlockingQueue<Action> getActions() {
        return getActions;
    }
}
