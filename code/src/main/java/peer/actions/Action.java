package peer.actions;

import peer.Connection;

import java.nio.ByteBuffer;

public abstract class Action {

    final protected Connection connection;
    protected Integer operation;

    Action(Connection connection) {
        this.operation = null;
        this.connection = connection;
        this.connection.setLockedToAction(true);
    }

    public Connection getConnection() {
        return this.connection;
    }

    public Integer getOperation() {
        return this.operation;
    }

    public abstract boolean execute(ByteBuffer byteBuffer);
}
