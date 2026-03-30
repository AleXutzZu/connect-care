package me.alexutzzu;

import me.alexutzzu.teledon.controller.AuthController;
import me.alexutzzu.teledon.controller.CharityController;
import me.alexutzzu.teledon.controller.DonorController;
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
import me.alexutzzu.teledon.service.CharityService;
import me.alexutzzu.teledon.service.DonorService;
import me.alexutzzu.teledon.service.mapper.CharityDtoEntityMapper;
import me.alexutzzu.teledon.service.mapper.DonorDtoEntityMapper;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            //Entity Mappers
            CharityDtoEntityMapper charityDtoEntityMapper = new CharityDtoEntityMapper();
            DonorDtoEntityMapper donorDtoEntityMapper = new DonorDtoEntityMapper();


            //DI setup
            CharityRepository charityRepository = DatabaseManager.getRepositoryInstance(CharityRepository.class, JdbcCharityRepositoryImpl.class);
            DonationRepository donationRepository = DatabaseManager.getRepositoryInstance(DonationRepository.class, JdbcDonationRepositoryImpl.class);
            DonorRepository donorRepository = DatabaseManager.getRepositoryInstance(DonorRepository.class, JdbcDonorRepositoryImpl.class);
            AuthUserRepository authUserRepository = DatabaseManager.getRepositoryInstance(AuthUserRepository.class, JdbcAuthUserRepositoryImpl.class);

            AuthService authService = new AuthService(authUserRepository);
            CharityService charityService = new CharityService(charityRepository, donationRepository, charityDtoEntityMapper);
            DonorService donorService = new DonorService(donorRepository, donorDtoEntityMapper);

            AuthController authController = new AuthController(authService);
            CharityController charityController = new CharityController(charityService);
            DonorController donorController = new DonorController(donorService);

            ClientManager clientManager = new ClientManager(List.of(authController, charityController, donorController));
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