package pinypon.connection;

import java.net.Socket;
import java.util.Comparator;

final public class ChatConnection implements Comparator<ChatConnection> {

    final public Socket socket;
    private long id;

    public ChatConnection(Socket socket) {
        this.socket = socket;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(this.id);
    }

    @Override
    public int compare(ChatConnection connection, ChatConnection otherConnection) {
        return Long.compare(connection.id, otherConnection.id);
    }

    @Override
    public boolean equals(Object obj) {
        ChatConnection otherConnection = (ChatConnection) obj;
        return otherConnection.id == this.id;
    }
}