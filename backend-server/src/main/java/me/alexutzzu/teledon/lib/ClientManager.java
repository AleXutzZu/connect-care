package me.alexutzzu.teledon.lib;

import me.alexutzzu.teledon.controller.AuthController;
import me.alexutzzu.teledon.protos.MainMessageProtos;
import me.alexutzzu.teledon.service.AuthService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class ClientManager {
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);
    private static final Logger logger = Logger.getLogger("me.alexutzzu.teledon.lib.ClientManager");
    private final Set<Client> clients = Collections.synchronizedSet(new HashSet<>());

    private final AuthController authController;

    public ClientManager(AuthController authController) {
        this.authController = authController;
    }

    private class Client implements Runnable {
        private final Socket socket;

        private Client(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (socket) {
                try (InputStream in = socket.getInputStream();
                     OutputStream out = socket.getOutputStream()) {
                    while (true) {
                        MainMessageProtos.MainMessage incoming = MainMessageProtos.MainMessage.parseDelimitedFrom(in);
                        if (incoming == null) break;

                        switch (incoming.getPayloadCase()) {
                            case AUTHREQ:
                                var response = authController.handleAuth(incoming.getAuthReq());

                                var outgoing = MainMessageProtos.MainMessage.newBuilder()
                                        .setAuthRes(response)
                                        .build();

                                outgoing.writeDelimitedTo(out);

                                break;

                            case PAYLOAD_NOT_SET:
                                logger.warning("Received empty payload, ignoring");
                                break;
                            default:
                                logger.warning("Unknown payload case: " + incoming.getPayloadCase());
                                break;
                        }
                    }
                } catch (IOException e) {
                    logger.severe("Connection error: " + e.getMessage());
                }

            } catch (IOException e) {
                logger.severe("Exception occurred: " + e.getMessage());
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
