package me.alexutzzu.teledon.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateDonorRequest(@NotBlank(message = "firstName cannot be empty") String firstName,
                                 @NotBlank(message = "lastName cannot be empty") String lastName,
                                 @NotBlank(message = "address cannot be empty") String address,

                                 @NotBlank(message = "phoneNumber cannot be empty")
                                 @Pattern(regexp = "\\d{10}")
                                 String phoneNumber) {
}
