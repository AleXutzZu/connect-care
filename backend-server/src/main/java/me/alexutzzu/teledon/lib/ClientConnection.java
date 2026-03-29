package me.alexutzzu.teledon.lib;

import me.alexutzzu.teledon.protos.MainMessageProtos;

public interface ClientConnection {
    void send(MainMessageProtos.MainMessage message);
    void broadcast(MainMessageProtos.MainMessage message);
}