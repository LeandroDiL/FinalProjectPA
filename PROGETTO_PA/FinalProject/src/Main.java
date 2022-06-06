import it.units.server.Server;

import java.io.IOException;


public class Main {
    public static void main(String[] args) {
        int port = 10001;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        int avaiableProcessors = Runtime.getRuntime().availableProcessors();

        Server server = new Server(port, "BYE", avaiableProcessors);
        try {
            server.start();
        } catch (IOException e) {
            System.err.printf("IOException: %s", e);
        }
    }
}