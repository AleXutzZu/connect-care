package me.alexutzzu.teledon.model.dto.statistics;

import java.time.LocalDateTime;

public interface DonationStatisticsDto {
    LocalDateTime getDate();

    Double getTotalAmount();

    Long getDonationCount();
}
