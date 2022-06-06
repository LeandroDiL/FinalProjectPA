package it.units.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final int port;
    private final String quitRequest;
    private final ExecutorService executorService;

    public Server(int port, String quitRequest, int concurrentClients) {
        this.port = port;
        this.quitRequest = quitRequest;
        executorService = Executors.newFixedThreadPool(concurrentClients);
    }

    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                try {
                    final Socket socket = serverSocket.accept();
                    ClientHandler clientHandler = new ClientHandler(socket, executorService, quitRequest);
                    clientHandler.start();
                } catch (IOException e) {
                    System.err.printf("Cannot accept connection due to %s", e);
                }
            }
        } finally {
            executorService.shutdown();
        }
    }

}
