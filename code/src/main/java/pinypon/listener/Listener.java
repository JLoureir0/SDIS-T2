package pinypon.listener;

import pinypon.handler.Handler;
import pinypon.utils.Defaults;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

final public class Listener extends Thread {

    private final DatagramSocket socket;
    private final Handler handler;
    private final byte[] packetBuffer;
    private final Thread handlerThread;
    private boolean interrupted = false;

    public Listener(int port) throws SocketException {
        this.socket = new DatagramSocket(port);
        this.packetBuffer = new byte[Defaults.UDP_BUFFER_SIZE];
        this.handler = new Handler();
        this.handlerThread = new Thread(this.handler);
        this.handlerThread.start();
    }

    public void run() {
        try {
            while (!interrupted) {
                DatagramPacket packet = new DatagramPacket(packetBuffer, packetBuffer.length);
                this.socket.receive(packet);
                this.handler.put(packet);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void interrupt() {
        interrupted = true;
        super.interrupt();
        this.socket.close();
        this.handler.put(null);
        this.handler.interrupt();
        try {
            this.handlerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

