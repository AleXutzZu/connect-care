package me.alexutzzu.teledon.model.dto;

import java.util.List;

public record CharityDto(Long id, String name, List<DonationDto> donations) {
}
