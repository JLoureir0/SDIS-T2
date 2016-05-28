package pinypon.interaction.gui;

import pinypon.protocol.chat.Message;

import java.util.concurrent.LinkedBlockingQueue;

public class MessageGuiWriter extends Thread {
    private boolean kill = false;
    private LinkedBlockingQueue<Message> messagesToPrint;
    private Gui gui;

    public MessageGuiWriter(Gui gui, LinkedBlockingQueue messagesToPrint) {
        this.gui = gui;
        this.messagesToPrint = messagesToPrint;
    }

    @Override
    public void run() {
        try {
            while(!kill) {
                Message message = messagesToPrint.take();
                this.gui.writeToTextArea(message.getSenderPublicKey().toString(), message.getBody());
                if (message == null) {
                    throw new InterruptedException();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void kill() {
        kill = true;
    }
}
