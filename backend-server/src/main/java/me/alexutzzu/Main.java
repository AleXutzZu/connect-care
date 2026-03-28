package me.alexutzzu;

import me.alexutzzu.teledon.controller.AuthController;
import me.alexutzzu.teledon.lib.ClientManager;
import me.alexutzzu.teledon.persistence.AuthUserRepository;
import me.alexutzzu.teledon.persistence.CharityRepository;
import me.alexutzzu.teledon.persistence.DonationRepository;
import me.alexutzzu.teledon.persistence.DonorRepository;
import me.alexutzzu.teledon.persistence.database.DatabaseManager;
import me.alexutzzu.teledon.persistence.impl.JdbcAuthUserRepositoryImpl;
import me.alexutzzu.teledon.persistence.impl.JdbcCharityRepositoryImpl;
import me.alexutzzu.teledon.persistence.impl.JdbcDonationRepositoryImpl;
import me.alexutzzu.teledon.persistence.impl.JdbcDonorRepositoryImpl;
import me.alexutzzu.teledon.service.AuthService;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        try {
            //DI setup
            CharityRepository charityRepository = DatabaseManager.getRepositoryInstance(CharityRepository.class, JdbcCharityRepositoryImpl.class);
            DonationRepository donationRepository = DatabaseManager.getRepositoryInstance(DonationRepository.class, JdbcDonationRepositoryImpl.class);
            DonorRepository donorRepository = DatabaseManager.getRepositoryInstance(DonorRepository.class, JdbcDonorRepositoryImpl.class);
            AuthUserRepository authUserRepository = DatabaseManager.getRepositoryInstance(AuthUserRepository.class, JdbcAuthUserRepositoryImpl.class);

            AuthService authService = new AuthService(authUserRepository);

            AuthController authController = new AuthController(authService);

            ClientManager clientManager = new ClientManager(authController);


            //Server setup
            int port = 8080;
            try (ServerSocket serverSocket = new ServerSocket()) {
                serverSocket.bind(new InetSocketAddress("localhost", port));

                while (!Thread.currentThread().isInterrupted()) {
                    Socket clientSocket = serverSocket.accept();
                    clientManager.registerClient(clientSocket);
                }
            } catch (IOException e) {
                System.err.println("Exception occurred: " + e.getMessage());
            } finally {
                clientManager.shutdown();
            }
        } catch (Exception e) {
            System.err.println("Exception occurred during run of the application");
        }
    }
}