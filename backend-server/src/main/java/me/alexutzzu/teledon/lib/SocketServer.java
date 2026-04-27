package me.alexutzzu.teledon.lib;

import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

@Component
public class SocketServer implements SmartLifecycle {
    private final ClientManager clientManager;
    private ServerSocket serverSocket;
    private volatile boolean running = false;
    private ExecutorService serverExecutor;

    private final Logger logger = Logger.getLogger("SocketServer");

    public SocketServer(ClientManager clientManager) {
        this.clientManager = clientManager;
    }

    @Override
    public void start() {
        this.running = true;
        this.serverExecutor = Executors.newSingleThreadExecutor();

        serverExecutor.execute(() -> {
            try {
                this.serverSocket = new ServerSocket(8080);
                while (running) {
                    Socket clientSocket = serverSocket.accept();
                    clientManager.registerClient(clientSocket);
                }
            } catch (IOException e) {
                if (running) logger.severe("Server socket error: " + e.getMessage());
            }
        });
    }

    @Override
    public void stop() {
        this.running = false;
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            logger.severe("Error closing socket: " + e.getMessage());
        }

        clientManager.shutdown();
        serverExecutor.shutdownNow();
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}
