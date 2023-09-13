package Server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class TimeServer {
    private static final int PORT = 12345;
    private static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10);
    private static final Set<PrintWriter> clientWriters = ConcurrentHashMap.newKeySet();

    public static void main(String[] args) {
        System.out.println("TimeServer is running...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                new ClientHandler(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private final Socket socket;
        private final PrintWriter out;

        public ClientHandler(Socket socket) {
            this.socket = socket;
            try {
                this.out = new PrintWriter(socket.getOutputStream(), true);
                clientWriters.add(out);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void run() {
            try {
                while (true) {
                    String currentTime = new Date().toString();
                    for (PrintWriter writer : clientWriters) {
                        writer.println(currentTime);
                    }
                    Thread.sleep(1000);
                }
            } catch ( InterruptedException e) {
                e.printStackTrace();
            } finally {
                clientWriters.remove(out);
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
