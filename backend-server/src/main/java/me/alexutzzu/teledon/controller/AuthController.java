package me.alexutzzu.teledon.controller;

import me.alexutzzu.teledon.exception.AuthenticationException;
import me.alexutzzu.teledon.protos.AuthUserProtos;
import me.alexutzzu.teledon.service.AuthService;

public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    public AuthUserProtos.AuthUserResponse handleAuth(AuthUserProtos.AuthUserRequest request) {
        try {
            boolean result = authService.checkCredentials(request.getUsername(), request.getPassword());

            if (result) {
                return AuthUserProtos.AuthUserResponse.newBuilder()
                        .setStatus(AuthUserProtos.AuthStatus.CREDENTIALS_OK)
                        .build();
            }

            return AuthUserProtos.AuthUserResponse.newBuilder()
                    .setStatus(AuthUserProtos.AuthStatus.FAILED_AUTH)
                    .setMessage("Credentials are invalid").build();

        } catch (AuthenticationException e) {
            return AuthUserProtos.AuthUserResponse.newBuilder()
                    .setStatus(AuthUserProtos.AuthStatus.FAILED_AUTH)
                    .setMessage("An error occurred: " + e.getMessage())
                    .build();
        }
    }
}
