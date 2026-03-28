package me.alexutzzu.teledon.service;

import me.alexutzzu.teledon.exception.AuthenticationException;
import me.alexutzzu.teledon.model.AuthUser;
import me.alexutzzu.teledon.persistence.AuthUserRepository;
import me.alexutzzu.teledon.protos.AuthUserProtos;

import java.sql.SQLException;
import java.util.Optional;

public class AuthService {
    private final AuthUserRepository authUserRepository;

    public AuthService(AuthUserRepository authUserRepository) {
        this.authUserRepository = authUserRepository;
    }

    public boolean checkCredentials(String username, String password) throws AuthenticationException {

        try {
            var user = authUserRepository.findByUsername(username);

            if (user.isEmpty()) return false;

            if (user.get().password().equals(password)) return true;

            return false;

        } catch (SQLException e) {
            throw new AuthenticationException("Error occurred during authentication: " + e.getMessage());
        }
    }
}
