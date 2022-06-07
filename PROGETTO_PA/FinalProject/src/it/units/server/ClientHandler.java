package it.units.server;

import it.units.request.ComputationRequestSolver;
import it.units.request.StatisticalRequest;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

public class ClientHandler extends Thread {
    private final Socket socket;
    private final ExecutorService executorService;
    private final String quitRequest;

    public ClientHandler(Socket socket, ExecutorService executorService, String quitRequest) {
        this.socket = socket;
        this.executorService = executorService;
        this.quitRequest = quitRequest;
    }

    @Override
    public void run() {
        try (socket) {
            System.out.printf("[%1$tY-%1$tm-%1$td %1$tT] New Client connection from %2$s.%n", System.currentTimeMillis(), socket.toString());
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            while (true) {
                String request = br.readLine();
                if (request == null) {
                    System.err.println("Client abruptly closed connection");
                    break;
                }
                if (request.equals("")) continue;
                if (request.equals(quitRequest)) {
                    System.out.printf("[%1$tY-%1$tm-%1$td %1$tT] Connection closed by %2$s", System.currentTimeMillis(), socket);
                    break;
                }
                if (request.startsWith("STAT_")) {
                    StatisticalRequest statRequest = new StatisticalRequest(request);
                    bw.write(statRequest.resolveStatRequest() + System.lineSeparator());
                    bw.flush();
                } else {
                    // computation request
                    executorService.submit(() -> {
                        try {
                            bw.write(ComputationRequestSolver.solveComputationRequest(request) + System.lineSeparator());
                            bw.flush();
                        } catch (IOException e) {
                            System.err.printf("IOException: %s%n", e.getMessage());
                        }
                    });
                }
            }
        } catch (IOException e) {
            System.err.printf("IO error: %s", e);
        }
    }
}
