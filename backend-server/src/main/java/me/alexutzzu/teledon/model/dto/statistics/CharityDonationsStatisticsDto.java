package me.alexutzzu.teledon.model.dto.statistics;

import java.time.LocalDateTime;

public interface CharityDonationsStatisticsDto {
    LocalDateTime getMonth();

    Long getDonorCount();

    Double getTotalAmount();
}
