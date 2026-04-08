package me.alexutzzu.teledon.controller;

import me.alexutzzu.teledon.lib.ClientConnection;
import me.alexutzzu.teledon.protos.MainMessageProtos;

public interface RequestHandler {
    MainMessageProtos.MainMessage.PayloadCase getHandlerType();

    void handleRequest(MainMessageProtos.MainMessage request, ClientConnection connection);
}
