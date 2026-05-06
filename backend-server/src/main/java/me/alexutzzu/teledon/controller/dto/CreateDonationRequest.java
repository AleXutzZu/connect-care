package me.alexutzzu.teledon.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record CreateDonationRequest(@NotBlank Long charityId,
                                    @NotBlank Long donorId,
                                    @Positive Double amount) {
}
