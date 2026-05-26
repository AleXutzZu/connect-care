package me.alexutzzu.teledon.model.dto;

import java.time.LocalDateTime;

public record DonorWithoutDonations(Long id, String firstName, String lastName, String address, String phoneNumber, LocalDateTime createdOn) {
}
