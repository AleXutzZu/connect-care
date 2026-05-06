package me.alexutzzu.teledon.model.dto;

import java.time.LocalDateTime;

public record DonationDto(Long id,
                          Double amount,
                          Long donorId,
                          String donorFirstName,
                          String donorLastName,
                          Long charityId,
                          String charityName,
                          LocalDateTime createdOn) {
}
