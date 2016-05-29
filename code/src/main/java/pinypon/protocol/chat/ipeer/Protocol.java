package pinypon.protocol.chat.ipeer;

import pinypon.connection.chat.ChatConnection;
import pinypon.protocol.NotifyingThread;
import pinypon.protocol.chat.Message;
import pinypon.user.Friend;
import pinypon.user.User;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Protocol extends NotifyingThread {
    private final ChatConnection chatConnection;
    private final User user;
    private final Friend friend;
    private boolean kill = false;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;

    public Protocol(User user, Friend friend, ChatConnection chatConnection) throws IOException {
        this.user = user;
        this.friend = friend;
        if (chatConnection == null) {
            throw new IllegalArgumentException("bad chatConnection");
        }
        this.chatConnection = chatConnection;
        this.objectOutputStream = new ObjectOutputStream(this.chatConnection.socket.getOutputStream());
        this.objectInputStream = new ObjectInputStream(this.chatConnection.socket.getInputStream());
    }

    @Override
    public void doRun() {
        try {
            while (!kill) {
                Object object = objectInputStream.readObject();
                if (object instanceof Message) {
                    Message message = (Message) object;
                    switch (message.getType()) {
                        case Message.END_MESSAGE:
                            return;
                        default:
                            throw new IllegalStateException("Bad Message");
                    }
                }
            }
        } catch (EOFException e) {
            System.out.println("Friend listener closed connection");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public synchronized void add(String message) throws InterruptedException, IOException {
        objectOutputStream.writeObject(new Message(Message.MESSAGE, message, user.getEncodedPublicKey()));
        objectOutputStream.flush();
    }

    public synchronized ChatConnection getChatConnection() {
        return chatConnection;
    }

    @Override
    protected synchronized void notifyListener() {
        this.listeningThread.notifyThreadComplete(this);
    }

    public synchronized void kill() {
        try {
            this.kill = true;
            super.interrupt();
            this.objectOutputStream.close();
            this.objectInputStream.close();
            this.chatConnection.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized Friend getFriend() {
        return friend;
    }
}
