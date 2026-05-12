package me.alexutzzu.teledon.model.dto;

import java.util.List;

public record CharityDto(Long id, String name, String username, Double target, String cause, List<DonationDto> donations) {
}
