package Peer;

import java.util.LinkedList;
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

        STATE state = connectionMessages.getFiniteAutomatonState();
        LinkedList<Message> inboundMessages = connectionMessages.getInboundMessages();
        LinkedList<Action> outboundMessages = connectionMessages.getOutboundMessages();
        for (Message message : inboundMessages) {
            Message.TYPE messageType = message.getType();
            switch (state) {
                case START:
                    if (messageType == Message.TYPE.SOME_TYPE_OF_MESSAGE) {
                        outboundMessages.add(new Action());
                        connectionMessages.setFiniteAutomatonState(STATE.SOMESTATE);
                    }
                    break;
                default:
                    throw new IllegalStateException("Unknown State");
            }
        }
        inboundMessages.clear();

        return outboundMessages.size();
    }
}
