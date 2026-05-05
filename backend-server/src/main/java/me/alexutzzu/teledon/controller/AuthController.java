package me.alexutzzu.teledon.controller;

import me.alexutzzu.teledon.controller.dto.LoginRequest;
import me.alexutzzu.teledon.service.TokenService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final TokenService tokenService;
    private final AuthenticationManager authManager;

    public AuthController(TokenService tokenService, AuthenticationManager authManager) {
        this.tokenService = tokenService;
        this.authManager = authManager;
    }


    @PostMapping("/login")
    public String login(@RequestBody LoginRequest loginRequest) {
        var auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()));

        return tokenService.generateToken(auth);
    }
}
