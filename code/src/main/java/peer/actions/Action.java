package peer.actions;

import peer.Connection;

import java.nio.ByteBuffer;

public abstract class Action {

    final protected Connection connection;

    Action(Connection connection) {
        this.connection = connection;
        this.connection.setLockedToAction(true);
    }

    public abstract boolean execute(ByteBuffer byteBuffer);
}
