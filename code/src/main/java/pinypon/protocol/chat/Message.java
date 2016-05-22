package pinypon.protocol.chat;

import pinypon.action.chat.CreateTCPConnection;
import pinypon.utils.Defaults;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.Socket;

public class Message implements Runnable {

    private static final int TYPE_INDEX = 0;
    private static final int LISTENING_PORT_INDEX = 1;

    private DatagramPacket packet;

    public Message(DatagramPacket packet) {
        this.packet = packet;
    }

    @Override
    public void run() {
        try {
            String[] packetData = new String(this.packet.getData(), this.packet.getOffset(), this.packet.getLength(), Defaults.ENCODING).split(Defaults.WHITESPACE_REGEX);
            String type = packetData[TYPE_INDEX];
            int listening_port = Integer.parseInt(packetData[LISTENING_PORT_INDEX]);

            Socket socket = CreateTCPConnection.create(this.packet.getAddress(), listening_port);


        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }

    }
}
