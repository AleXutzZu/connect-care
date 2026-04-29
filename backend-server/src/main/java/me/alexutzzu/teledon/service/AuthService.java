package me.alexutzzu.teledon.service;

import me.alexutzzu.teledon.persistence.AuthUserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final AuthUserRepository authUserRepository;

    public AuthService(AuthUserRepository authUserRepository) {
        this.authUserRepository = authUserRepository;
    }

    public boolean checkCredentials(String username, String password) {

        var user = authUserRepository.findAuthUserByUsername(username);

        return user.map(authUser -> authUser.getPassword().equals(password)).orElse(false);
    }
}
