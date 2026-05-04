package me.alexutzzu.teledon.controller;

import me.alexutzzu.teledon.service.AuthService;
import org.springframework.stereotype.Controller;

@Controller("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }


}
