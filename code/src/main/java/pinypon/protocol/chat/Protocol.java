package pinypon.protocol.chat;

import pinypon.protocol.NotifyingThread;
import pinypon.connection.ChatConnection;

import java.io.IOException;

final public class Protocol extends NotifyingThread {

    private boolean kill = false;
    private final ChatConnection chatConnection;

    public Protocol(ChatConnection chatConnection) {
        if (chatConnection == null) {
            throw new IllegalArgumentException("bad chatConnection");
        }
        this.chatConnection = chatConnection;
    }

    @Override
    public void doRun() {
        // TODO
//        boolean reachedFinalState = false;
//        ChatHeaderReader chatHeaderReader = new ChatHeaderReader();
//
//        while(!reachedFinalState && !kill) {
//
//            chatHeaderReader.parse();
//
//            switch (connectionState) {
//                case START:
//                    switch (messageType) {
//                        case FILE_RECEIVE:
//                            connection.setState(STATE.START);
//                            try {
//                                return new ActionOnFileReceive(connection, "path/where/to/save/file");
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                                return;
//                            }
//                    }
//                    break;
//                default:
//                    try {
//                        chatConnection.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    throw new IllegalStateException("Unknown State");
//            }
//        }
    }

    public ChatConnection getChatConnection() {
        return chatConnection;
    }

    @Override
    protected void notifyListener() {
        this.listeningThread.notifyThreadComplete(this);
    }

    public void kill() {
        this.kill = true;
    }

    public enum STATE {
        START,
        ACK,
        SOMESTATE
    }
}
