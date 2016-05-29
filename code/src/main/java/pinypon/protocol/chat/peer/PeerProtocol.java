package pinypon.protocol.chat.peer;

import pinypon.connection.chat.ChatConnection;
import pinypon.interaction.gui.Gui;
import pinypon.protocol.NotifyingThread;
import pinypon.protocol.chat.Message;
import pinypon.user.User;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.LinkedBlockingQueue;

final public class PeerProtocol extends NotifyingThread {

    private final User user;
    private final ObjectInputStream objectInputStream;
    private final ObjectOutputStream objectOutputStream;
    private final ChatConnection chatConnection;
    private boolean kill = false;
    private final Gui gui;

    public PeerProtocol(User user, ChatConnection chatConnection, Gui gui) throws IOException {
        if (chatConnection == null) {
            throw new IllegalArgumentException("bad chatConnection");
        }
        this.chatConnection = chatConnection;

        this.objectInputStream = new ObjectInputStream(chatConnection.socket.getInputStream());
        this.objectOutputStream = new ObjectOutputStream(chatConnection.socket.getOutputStream());
        this.user = user;
        this.gui = gui;
    }

    @Override
    public void doRun() {
        try {
            boolean reachedFinalState = false;

            while (!reachedFinalState && !kill) {

                Object objectReceived = objectInputStream.readObject();
                if (objectReceived instanceof Message) {
                    Message message = (Message) objectReceived;
                    switch (message.getType()) {
                        case Message.MESSAGE:
                            this.gui.writeToTextArea(message.getEncodedSenderPublicKey(), message.getBody());
                            break;
                        case Message.FRIEND_REQUEST:
                            this.gui.addFriendPeer(message.getEncodedSenderPublicKey(), message.getBody());
                        default:
                            throw new IllegalStateException("Unknown header type");
                    }
                } else {
                    throw new IllegalStateException("Illegal Object");
                }
            }
        } catch (EOFException e) {
            System.out.println("Friend closed connection");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public synchronized ChatConnection getChatConnection() {
        return chatConnection;
    }

    public synchronized void send(Message message) throws InterruptedException, IOException {
        objectOutputStream.writeObject(message);
        objectOutputStream.flush();
    }

    @Override
    protected synchronized void notifyListener() {
        this.listeningThread.notifyThreadComplete(this);
    }

    public synchronized void kill() {
        try {
            objectOutputStream.writeObject(
                    new Message(Message.END_MESSAGE, null, this.user.getEncodedPublicKey())
            );
            objectOutputStream.flush();
            this.kill = true;
            super.interrupt();
            objectInputStream.close();
            objectOutputStream.close();
            chatConnection.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
