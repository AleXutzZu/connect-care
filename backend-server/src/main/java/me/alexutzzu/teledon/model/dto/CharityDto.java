package me.alexutzzu.teledon.model.dto;

import java.util.List;

public record CharityDto(Long id, String name, String registeredBy, Double target, List<DonationDto> donations) {
}
