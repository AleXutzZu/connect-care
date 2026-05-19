package me.alexutzzu.teledon.controller.dto.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import me.alexutzzu.teledon.model.dto.DonorDto;

import java.util.Optional;

@AllArgsConstructor
@Builder
public class DonorEvent implements SseEvent<DonorDto> {

    private final EventType eventType;
    private final DonorDto payload;
    private final Long id;

    @Override
    public EventType getType() {
        return eventType;
    }

    @Override
    public Optional<DonorDto> getPayload() {
        return Optional.ofNullable(payload);
    }

    @Override
    public Long getId() {
        return id;
    }

    public static DonorEvent created(DonorDto payload) {
        return DonorEvent.builder()
                .eventType(EventType.CREATED)
                .payload(payload)
                .id(payload.id())
                .build();
    }

    public static DonorEvent updated(DonorDto payload) {
        return DonorEvent.builder()
                .eventType(EventType.UPDATED)
                .payload(payload)
                .id(payload.id())
                .build();
    }

    public static DonorEvent deleted(Long id) {
        return DonorEvent.builder()
                .eventType(EventType.DELETED)
                .id(id)
                .build();
    }
}
