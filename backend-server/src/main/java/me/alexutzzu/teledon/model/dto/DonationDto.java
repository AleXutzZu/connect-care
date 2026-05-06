package me.alexutzzu.teledon.model.dto;

public record DonationDto(Long id, Double amount, Long donorId, String donorFirstName, String donorLastName, Long charityId, String charityName) {
}
