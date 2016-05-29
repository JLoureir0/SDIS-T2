package pinypon.protocol.chat.peer;

import pinypon.connection.chat.ChatConnection;
import pinypon.protocol.NotifyingThread;
import pinypon.protocol.chat.Message;
import pinypon.user.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.LinkedBlockingQueue;

final public class Protocol extends NotifyingThread {

    private final User user;
    private final ObjectInputStream objectInputStream;
    private final ObjectOutputStream objectOutputStream;
    private final LinkedBlockingQueue<Message> messagesToPrint;
    private final ChatConnection chatConnection;
    private boolean kill = false;

    public Protocol(User user, ChatConnection chatConnection, LinkedBlockingQueue<Message> messagesToPrint) throws IOException {
        if (chatConnection == null) {
            throw new IllegalArgumentException("bad chatConnection");
        }
        this.chatConnection = chatConnection;
        this.messagesToPrint = messagesToPrint;

        this.objectInputStream = new ObjectInputStream(chatConnection.socket.getInputStream());
        this.objectOutputStream = new ObjectOutputStream(chatConnection.socket.getOutputStream());
        this.user = user;
    }

    @Override
    public void doRun() {
        try {
            boolean reachedFinalState = false;

            while (!reachedFinalState && !kill) {

                Object objectReceived = objectInputStream.readObject();
                Message message;

                if (objectReceived instanceof Message) {
                    message = (Message) objectReceived;
                } else {
                    throw new IllegalStateException("Illegal Object");
                }

                switch (message.getType()) {
                    case Message.MESSAGE:
                        this.messagesToPrint.put(message);
                        break;
                    default:
                        objectInputStream.close();
                        objectOutputStream.close();
                        chatConnection.socket.close();
                        throw new IllegalStateException("Unknown header type");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                objectInputStream.close();
                objectOutputStream.close();
                chatConnection.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
