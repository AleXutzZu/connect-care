package me.alexutzzu.teledon.controller.dto.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import me.alexutzzu.teledon.model.dto.CharityDto;
import me.alexutzzu.teledon.model.dto.CharityWithRaisedSum;

import java.util.Optional;


@Builder
@AllArgsConstructor
public class CharityEvent implements SseEvent<CharityDto> {

    private final EventType eventType;
    private final CharityDto payload;

    private final Long id;

    @Override
    public EventType getType() {
        return eventType;
    }

    @Override
    public Optional<CharityDto> getPayload() {
        return Optional.ofNullable(payload);
    }

    @Override
    public Long getId() {
        return id;
    }

    public static CharityEvent created(CharityDto payload) {
        return CharityEvent.builder()
                .eventType(EventType.CREATED)
                .payload(payload)
                .id(payload.id())
                .build();
    }

    public static CharityEvent updated(CharityDto payload) {
        return CharityEvent.builder()
                .eventType(EventType.UPDATED)
                .payload(payload)
                .id(payload.id())
                .build();
    }

    public static CharityEvent deleted(Long id) {
        return CharityEvent.builder()
                .eventType(EventType.DELETED)
                .id(id)
                .build();
    }
}
