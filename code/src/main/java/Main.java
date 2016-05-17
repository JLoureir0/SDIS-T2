import peer.Server;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {

        int server_port = 54321;
        Server server = new Server(server_port);
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }


        CommandLineInterface cliInterface = new CommandLineInterface();
    }
}
