package Peer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class ConnectionsWriter implements Runnable {

    final private ConcurrentHashMap<Long, Connection> activeConnections;
    final private LinkedBlockingQueue<Message> outboundMessages;

    final private Selector writeSelector;
    final private ByteBuffer interimWriteBuffer;


    public ConnectionsWriter(ConcurrentHashMap<Long, Connection> activeConnections, int bufferSize) throws IOException {
        this.activeConnections = activeConnections;
        this.outboundMessages = new LinkedBlockingQueue<>();
        this.writeSelector = Selector.open();
        this.interimWriteBuffer = ByteBuffer.allocateDirect(bufferSize);
    }

    @Override
    public void run() {
        
    }

    public LinkedBlockingQueue<Message> getOutboundMessages() {
        return outboundMessages;
    }
}
