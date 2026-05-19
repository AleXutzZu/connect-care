package me.alexutzzu.teledon.controller.dto.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import me.alexutzzu.teledon.model.dto.DonationDto;
import me.alexutzzu.teledon.model.dto.DonorDto;

import java.util.Optional;

@AllArgsConstructor
@Builder
public class DonationEvent implements SseEvent<DonationDto> {

    private final EventType eventType;
    private final DonationDto payload;
    private final Long id;

    @Override
    public EventType getType() {
        return eventType;
    }

    @Override
    public Optional<DonationDto> getPayload() {
        return Optional.ofNullable(payload);
    }

    @Override
    public Long getId() {
        return id;
    }

    public static DonationEvent created(DonationDto payload) {
        return DonationEvent.builder()
                .eventType(EventType.CREATED)
                .payload(payload)
                .id(payload.id())
                .build();
    }

    public static DonationEvent updated(DonationDto payload) {
        return DonationEvent.builder()
                .eventType(EventType.UPDATED)
                .payload(payload)
                .id(payload.id())
                .build();
    }

    public static DonationEvent deleted(Long id) {
        return DonationEvent.builder()
                .eventType(EventType.DELETED)
                .id(id)
                .build();
    }
}
