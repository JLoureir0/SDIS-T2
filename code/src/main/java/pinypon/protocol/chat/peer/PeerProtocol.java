package pinypon.protocol.chat.peer;

import javafx.application.Platform;
import pinypon.connection.chat.ChatConnection;
import pinypon.handler.chat.peer.PeerHandler;
import pinypon.interaction.gui.Gui;
import pinypon.protocol.NotifyingThread;
import pinypon.protocol.chat.Message;
import pinypon.user.User;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

final public class PeerProtocol extends NotifyingThread {

    private final User user;
    private final ObjectInputStream objectInputStream;
    private final ObjectOutputStream objectOutputStream;
    private final ChatConnection chatConnection;
    private final Gui gui;
    private final String encodedFriendPublicKey;
    private boolean kill = false;

    public PeerProtocol(User user, ChatConnection chatConnection, Gui gui, PeerHandler peerHandler) throws IOException, ClassNotFoundException {
        if (chatConnection == null) {
            throw new IllegalArgumentException("bad chatConnection");
        }
        this.chatConnection = chatConnection;

        this.objectInputStream = new ObjectInputStream(chatConnection.socket.getInputStream());
        this.objectOutputStream = new ObjectOutputStream(chatConnection.socket.getOutputStream());
        Object firstMessage = objectInputStream.readObject();
        Message message = (Message) firstMessage;
        this.user = user;
        this.gui = gui;
        this.encodedFriendPublicKey = message.getEncodedSenderPublicKey();
        peerHandler.addConnection(encodedFriendPublicKey,this);

        this.messageHandler(firstMessage);

    }

    @Override
    public void doRun() {
        try {
            boolean reachedFinalState = false;

            while (!reachedFinalState && !kill) {
                messageHandler(objectInputStream.readObject());
            }
        } catch (EOFException e) {
            System.out.println("Friend closed connection");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void messageHandler(Object objectReceived) throws IOException, ClassNotFoundException {
        if (objectReceived instanceof Message) {
            Message message = (Message) objectReceived;
            switch (message.getType()) {
                case Message.MESSAGE:
                    Platform.runLater(() -> gui.writeToTextArea(message.getEncodedSenderPublicKey(), message.getBody()));
                    break;
                case Message.FRIEND_REQUEST:
                    Platform.runLater(() -> gui.addFriendPeer(message.getEncodedSenderPublicKey(), message.getBody()));
                    break;
                default:
                    throw new IllegalStateException("Unknown header type");
            }
        } else {
            throw new IllegalStateException("Illegal Object");
        }
    }

    public synchronized ChatConnection getChatConnection() {
        return chatConnection;
    }

    public synchronized void send(Message message) throws InterruptedException, IOException {
        objectOutputStream.writeObject(message);
        System.out.println(message.getType());
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

    public String getEncodedFriendPublicKey() {
        return encodedFriendPublicKey;
    }
}
