package me.alexutzzu.teledon.model.dto.statistics;

import java.time.LocalDateTime;

public interface CharityStatisticsDto {
    LocalDateTime getMonth();

    Long getDonorCount();

    Double getTotalAmount();
}
