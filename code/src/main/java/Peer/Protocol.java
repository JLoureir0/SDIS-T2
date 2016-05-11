package Peer;

import java.util.LinkedList;

final public class Protocol {
    public enum STATE {
        START,
        ACK,
        SOMESTATE
    }

    static int transition(ConnectionMessages connectionMessages) {

        STATE state = connectionMessages.getFiniteAutomatonState();
        LinkedList<Message> inboundMessages = connectionMessages.getInboundMessages();
        LinkedList<MessageAction> outboundMessages = connectionMessages.getOutboundMessages();
        for (Message message : inboundMessages) {
            Message.TYPE messageType = message.getType();
            switch (state) {
                case START:
                    if (messageType == Message.TYPE.SOME_TYPE_OF_MESSAGE) {
                        outboundMessages.add(new MessageAction());
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
