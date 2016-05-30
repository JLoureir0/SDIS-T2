package pinypon.utils;

public class Tracker {
    public final String id;
    public final String ip;
    public final String port;
    public final String username;

    public Tracker(String id, String ip, String port, String username) {
        this.id = id;
        this.ip = ip;
        this.port = port;
        this.username = username;
    }

    @Override
    public String toString() {
        return this.id + " " + " " + ip + " " + port + " " + username;
    }
}
