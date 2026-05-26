package me.alexutzzu.teledon.model.dto.statistics;

import java.time.LocalDateTime;
import java.util.Optional;


public record DonorStatisticsDto(int totalDonations,
                                 Optional<Double> averageDonation,
                                 Optional<HighestDonationInfoDto> highestDonation,
                                 Optional<LocalDateTime> lastDonation) {
}
