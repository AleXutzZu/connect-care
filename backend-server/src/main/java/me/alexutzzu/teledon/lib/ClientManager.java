package me.alexutzzu.teledon.lib;

import me.alexutzzu.teledon.controller.AuthController;
import me.alexutzzu.teledon.controller.CharityController;
import me.alexutzzu.teledon.controller.RequestHandler;
import me.alexutzzu.teledon.protos.MainMessageProtos;
import me.alexutzzu.teledon.service.AuthService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class ClientManager {
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);
    private static final Logger logger = Logger.getLogger("me.alexutzzu.teledon.lib.ClientManager");
    private final Set<Client> clients = Collections.synchronizedSet(new HashSet<>());

    private final List<RequestHandler> controllers;

    public ClientManager(List<RequestHandler> controllers) {
        this.controllers = Collections.unmodifiableList(controllers);
    }

    private class Client implements Runnable, ClientConnection {
        private final Socket socket;
        private OutputStream out;

        private Client(Socket socket) {
            this.socket = socket;
        }

        @Override
        public synchronized void send(MainMessageProtos.MainMessage message) {
            if (out != null) {
                try {
                    message.writeDelimitedTo(out);
                } catch (IOException e) {
                    logger.warning("Failed to send message: " + e.getMessage());
                }
            }
        }

        @Override
        public void broadcast(MainMessageProtos.MainMessage message) {
            synchronized (clients) {
                for (Client otherClient : clients) {
                    if (otherClient != this) {
                        otherClient.send(message);
                    }
                }
            }
        }

        @Override
        public void run() {
            try (socket) {
                try (InputStream in = socket.getInputStream();
                     OutputStream out = socket.getOutputStream()) {
                    synchronized (this) {
                        this.out = out;
                    }
                    while (true) {
                        MainMessageProtos.MainMessage incoming = MainMessageProtos.MainMessage.parseDelimitedFrom(in);
                        if (incoming == null) break;

                        var correctHandler = controllers.stream().filter(c -> c.getHandlerType() == incoming.getPayloadCase()).findFirst();

                        if (correctHandler.isEmpty()) {
                            logger.warning("Cannot handle payload " + incoming.getPayloadCase());
                            continue;
                        }

                        correctHandler.get().handleRequest(incoming, this);
                    }
                } catch (IOException e) {
                    logger.severe("Connection error: " + e.getMessage());
                }

            } catch (IOException e) {
                logger.severe("Exception occurred: " + e.getMessage());
            } finally {
                clients.remove(this);
            }
        }
    }


    public void registerClient(Socket clientSocket) {
        logger.info("New connection from: " + clientSocket.getRemoteSocketAddress());
        Client client = new Client(clientSocket);

        clients.add(client);
        executorService.execute(client);
    }

    public void shutdown() {
        executorService.shutdown();
    }
}
