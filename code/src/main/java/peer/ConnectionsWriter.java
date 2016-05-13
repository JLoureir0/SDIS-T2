package peer;

import peer.actions.Action;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class ConnectionsWriter implements Runnable {

    final private ConcurrentHashMap<Long, Connection> activeConnections;
    final private LinkedBlockingQueue<Action> actions;

    final private Selector writeSelector;
    final private ByteBuffer interimWriteBuffer;


    public ConnectionsWriter(ConcurrentHashMap<Long, Connection> activeConnections) throws IOException {
        this.activeConnections = activeConnections;
        this.actions = new LinkedBlockingQueue<>();
        this.writeSelector = Selector.open();
        this.interimWriteBuffer = ByteBuffer.allocateDirect(Constants.BYTE_BUFFER_SIZE);
    }

    @Override
    public void run() {
        Iterator<Action> actionIterator = actions.iterator();
        while (actionIterator.hasNext()) {
            Action action = actionIterator.next();
            if (action.execute(this.interimWriteBuffer)) {
                actionIterator.remove();
            }
        }

        try {
            if (this.writeSelector.selectNow() > 0) {
                Set<SelectionKey> selectionKeys = this.writeSelector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectionKeys.iterator();

                while (keyIterator.hasNext()) {
                    SelectionKey selectionKey = keyIterator.next();
                    Action action = (Action) selectionKey.attachment();
                    if (action.execute(this.interimWriteBuffer)) {
                        actionIterator.remove();
                        selectionKey.cancel();
                    }
                    keyIterator.remove();
                }
                selectionKeys.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void put(Action action) throws ClosedChannelException {
        Connection connection = action.getConnection();
        connection.register(this.writeSelector, SelectionKey.OP_WRITE, action);
    }
}
