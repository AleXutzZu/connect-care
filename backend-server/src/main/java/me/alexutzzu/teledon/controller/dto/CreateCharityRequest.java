package me.alexutzzu.teledon.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateCharityRequest(@NotBlank(message = "name cannot be empty") String name) {
}
