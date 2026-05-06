package me.alexutzzu.teledon.controller.dto;

import jakarta.validation.constraints.Positive;

public record CreateDonationRequest(Long charityId,
                                    Long donorId,
                                    @Positive Double amount) {
}
