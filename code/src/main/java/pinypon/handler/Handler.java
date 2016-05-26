package pinypon.handler;

import pinypon.protocol.chat.File;
import pinypon.protocol.chat.Message;
import pinypon.utils.Defaults;

import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.util.concurrent.LinkedBlockingQueue;

final public class Handler implements Runnable {

    final private LinkedBlockingQueue<DatagramPacket> queuedRequests;

    public Handler() {
        this.queuedRequests = new LinkedBlockingQueue<>();
    }

    public void run() {
        while (true) {
            try {
                DatagramPacket packet = this.queuedRequests.take();
                this.handlePacket(packet);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void put(DatagramPacket packet) {
        try {
            this.queuedRequests.put(packet);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void handlePacket(DatagramPacket packet) {
        try {
            String packetData = new String(packet.getData(), packet.getOffset(), packet.getLength(), Defaults.ENCODING);
            String protocol = packetData.substring(0, packetData.indexOf(" "));

            switch (protocol) {
                case Defaults.PROTOCOL_CHAT_MESSAGE:
                    new Message(packet).run();
                    break;
                case Defaults.PROTOCOL_CHAT_FILE:
                    new File(packet).run();
                    break;
                default:
                    System.err.println("Unsupported protocol: " + protocol);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
