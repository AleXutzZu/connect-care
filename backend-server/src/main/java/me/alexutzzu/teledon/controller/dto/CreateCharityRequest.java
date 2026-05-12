package me.alexutzzu.teledon.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record CreateCharityRequest(@NotBlank(message = "name cannot be empty") String name, @Positive(message = "target must be a positive number") Double target, String cause) {
}
