package pinypon.listener;

import pinypon.handler.Handler;
import pinypon.utils.Defaults;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

final public class Listener implements Runnable {

    private DatagramSocket socket;
    private Handler handler;
    private byte[] packetBuffer;

    public Listener(int port) throws SocketException {
        this.socket = new DatagramSocket(port);
        this.packetBuffer = new byte[Defaults.UDP_BUFFER_SIZE];
        this.handler = new Handler();
        new Thread(this.handler).start();
    }

    public void run() {
        while (true) {
            DatagramPacket packet =  new DatagramPacket(packetBuffer, packetBuffer.length);
            try {
                this.socket.receive(packet);
                this.handler.put(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

