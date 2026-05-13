package me.alexutzzu.teledon.model.dto;

import java.time.LocalDateTime;
import java.util.List;

public record DonorDto(Long id, String firstName, String lastName, String address, String phoneNumber, List<DonationDto> donations, LocalDateTime createdOn) {
}
