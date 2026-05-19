package me.alexutzzu.teledon.controller.dto.events;

import java.util.Optional;

public interface SseEvent<T> {
    EventType getType();

    Optional<T> getPayload();

    Long getId();
}
