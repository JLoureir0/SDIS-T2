package pinypon.listener;

import pinypon.handler.Handler;
import pinypon.utils.Defaults;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

final public class Listener implements Runnable {

    private final DatagramSocket socket;
    private final Handler handler;
    private final byte[] packetBuffer;
    private final Thread handlerThread;
    private boolean kill = false;

    public Listener(int port) throws SocketException {
        this.socket = new DatagramSocket(port);
        this.packetBuffer = new byte[Defaults.UDP_BUFFER_SIZE];
        this.handler = new Handler();
        this.handlerThread = new Thread(this.handler);
        this.handlerThread.start();
    }

    public void run() {
        try {
            while (!kill) {
                DatagramPacket packet = new DatagramPacket(packetBuffer, packetBuffer.length);
                this.socket.receive(packet);
                this.handler.put(packet);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void kill() {
        kill = true;
        Thread.currentThread().interrupt();
        this.socket.disconnect();
        this.socket.close();
        this.handler.kill();
    }
}

