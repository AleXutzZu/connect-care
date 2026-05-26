package me.alexutzzu.teledon.model.dto.statistics;

import java.util.List;

public record GeneralStatisticsDto(MonthToDateDonorStatisticsDto monthToDateDonors,
                                   MonthlyActiveDonorStatisticsDto monthlyActiveDonors,
                                   List<DonationStatisticsDto> dailyDonations) {
}
