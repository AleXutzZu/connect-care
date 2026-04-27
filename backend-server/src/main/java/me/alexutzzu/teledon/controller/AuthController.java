package me.alexutzzu.teledon.controller;

import me.alexutzzu.teledon.lib.ClientConnection;
import me.alexutzzu.teledon.protos.AuthUserProtos;
import me.alexutzzu.teledon.protos.MainMessageProtos;
import me.alexutzzu.teledon.protos.ResponseStatusProtos;
import me.alexutzzu.teledon.service.AuthService;
import org.springframework.stereotype.Controller;

@Controller
public class AuthController implements RequestHandler {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    private AuthUserProtos.AuthUserResponse handleAuth(AuthUserProtos.AuthUserRequest request) {
        boolean result = authService.checkCredentials(request.getUsername(), request.getPassword());

        if (result) {
            return AuthUserProtos.AuthUserResponse.newBuilder()
                    .setStatus(ResponseStatusProtos.ResponseStatus.OK)
                    .build();
        }

        return AuthUserProtos.AuthUserResponse.newBuilder()
                .setStatus(ResponseStatusProtos.ResponseStatus.FAILED)
                .setMessage("Credentials are invalid").build();

    }

    @Override
    public MainMessageProtos.MainMessage.PayloadCase getHandlerType() {
        return MainMessageProtos.MainMessage.PayloadCase.AUTHREQ;
    }

    @Override
    public void handleRequest(MainMessageProtos.MainMessage request, ClientConnection connection) {
        var response = handleAuth(request.getAuthReq());

        connection.send(MainMessageProtos.MainMessage.newBuilder().setAuthRes(response).build());
    }
}
