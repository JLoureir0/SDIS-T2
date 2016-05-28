package pinypon.interaction.gui;

import pinypon.protocol.chat.Message;

import java.util.concurrent.LinkedBlockingQueue;

public class GuiMessagePrinter extends Thread {
    private boolean kill = false;
    private LinkedBlockingQueue<Message> messagesToPrint;
    private Gui gui;

    public GuiMessagePrinter(Gui gui, LinkedBlockingQueue messagesToPrint) {
        this.gui = gui;
        this.messagesToPrint = messagesToPrint;
    }

    @Override
    public void run() {
        try {
            while (!kill) {
                Message message = messagesToPrint.take();
                if (message == null) {
                    throw new InterruptedException();
                }
                this.gui.writeToTextArea(message.getEncodedSenderPublicKey(), message.getBody());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void kill() {
        kill = true;
        super.interrupt();
    }
}
