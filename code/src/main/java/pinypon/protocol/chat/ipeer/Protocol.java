package pinypon.protocol.chat.ipeer;

import pinypon.connection.chat.ChatConnection;
import pinypon.protocol.NotifyingThread;
import pinypon.protocol.chat.Message;
import pinypon.user.Friend;
import pinypon.user.User;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.LinkedBlockingQueue;

public class Protocol extends NotifyingThread {
    private final LinkedBlockingQueue<String> messagesToSend;
    private final ChatConnection chatConnection;
    private final User user;
    private final Friend friend;
    private boolean interrupted = false;

    public Protocol(User user, Friend friend, ChatConnection chatConnection) {
        this.user = user;
        this.friend = friend;
        if (chatConnection == null) {
            throw new IllegalArgumentException("bad chatConnection");
        }
        this.chatConnection = chatConnection;
        this.messagesToSend = new LinkedBlockingQueue<>();
    }

    @Override
    public void doRun() {
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(this.chatConnection.socket.getOutputStream());
            while (!interrupted) {
                String messageString = this.messagesToSend.take();
                if (messageString == null) {
                    throw new InterruptedException();
                }
                Message message = new Message(Message.MESSAGE, messageString, user.getPublicKey());
                objectOutputStream.writeObject(message);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void add(String message) throws InterruptedException {
        this.messagesToSend.put(message);
    }

    public ChatConnection getChatConnection() {
        return chatConnection;
    }

    @Override
    protected void notifyListener() {
        this.listeningThread.notifyThreadComplete(this);
    }

    public void kill() {
        this.interrupted = true;
        super.interrupt();
        messagesToSend.add(null);
    }
}
