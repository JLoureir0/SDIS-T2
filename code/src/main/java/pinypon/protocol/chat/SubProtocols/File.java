package pinypon.protocol.chat.SubProtocols;

import java.net.DatagramPacket;

public class File implements Runnable {

    private DatagramPacket packet;

    public File(DatagramPacket packet) {
        this.packet = packet;
    }

    @Override
    public void run() {

    }
}