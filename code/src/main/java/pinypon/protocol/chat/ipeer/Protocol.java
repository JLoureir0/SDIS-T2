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
    private ObjectOutputStream objectOutputStream;

    public Protocol(User user, Friend friend, ChatConnection chatConnection) throws IOException {
        this.user = user;
        this.friend = friend;
        if (chatConnection == null) {
            throw new IllegalArgumentException("bad chatConnection");
        }
        this.chatConnection = chatConnection;
        this.messagesToSend = new LinkedBlockingQueue<>();
        this.objectOutputStream = new ObjectOutputStream(this.chatConnection.socket.getOutputStream());
    }

    @Override
    public void doRun() {
        try {
            while (!interrupted) {
                String messageString = this.messagesToSend.take();
                if (messageString == null) {
                    return;
                }
                Message message = new Message(Message.MESSAGE, messageString, user.getEncodedPublicKey());
                objectOutputStream.writeObject(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
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
        try {
            this.interrupted = true;
            super.interrupt();
            messagesToSend.clear();
            messagesToSend.add(null);
            this.objectOutputStream.close();
            this.chatConnection.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Friend getFriend() {
        return friend;
    }
}
