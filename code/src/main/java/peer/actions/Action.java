package peer.actions;

import peer.Connection;

import java.nio.ByteBuffer;

public abstract class Action {

    final protected Connection connection;
    protected Integer operation;
    protected boolean newOperation = true;

    Action(Connection connection) {
        this.operation = null;
        this.connection = connection;
        this.connection.setLockedToAction(true);
    }

    public Connection getConnection() {
        return this.connection;
    }

    public Integer getOperation() {
        this.newOperation = false;
        return this.operation;
    }

    public boolean newOperation() {
        return newOperation;
    }

    public abstract boolean execute(ByteBuffer byteBuffer);

}
