package me.alexutzzu.teledon.service;

import me.alexutzzu.teledon.exception.NotFoundException;
import me.alexutzzu.teledon.model.dto.statistics.*;
import me.alexutzzu.teledon.persistence.CharityRepository;
import me.alexutzzu.teledon.persistence.DonationRepository;
import me.alexutzzu.teledon.persistence.DonorRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class StatisticsService {
    private final CharityRepository charityRepository;
    private final DonationRepository donationRepository;
    private final DonorRepository donorRepository;

    public StatisticsService(CharityRepository charityRepository, DonationRepository donationRepository, DonorRepository donorRepository) {
        this.charityRepository = charityRepository;
        this.donationRepository = donationRepository;
        this.donorRepository = donorRepository;
    }

    public List<CharityDonationsStatisticsDto> getDonationStatisticsForCharitySince(Long charityId, LocalDateTime since) {
        if (charityRepository.findById(charityId).isEmpty()) throw new NotFoundException();

        return charityRepository.findMonthlyStatsByCharity(charityId, since);
    }

    public List<DonationStatisticsDto> getDailyDonationStats(LocalDateTime since) {
        return donationRepository.findDailyStats(since);
    }

    public MonthToDateDonorStatisticsDto getMonthToDateDonorStatistics() {
        LocalDate today = LocalDate.now();
        LocalDate firstDayOfCurrentMonth = today.withDayOfMonth(1);
        LocalDate firstDayOfPreviousMonth = firstDayOfCurrentMonth.minusMonths(1);

        long currentMonthDonors = donorRepository.countByCreatedOnBetween(firstDayOfCurrentMonth.atStartOfDay(), today.atTime(23, 59, 59));
        long previousMonthDonors = donorRepository.countByCreatedOnBetween(firstDayOfPreviousMonth.atStartOfDay(), firstDayOfPreviousMonth.plusDays(today.getDayOfMonth() - 1).atTime(23, 59, 59));

        return new MonthToDateDonorStatisticsDto(currentMonthDonors, previousMonthDonors);
    }

    public MonthlyActiveDonorStatisticsDto getMonthlyActiveDonorStatistics() {
        LocalDateTime endOfCurrentWindow = LocalDateTime.now();
        LocalDateTime startOfCurrentWindow = endOfCurrentWindow.minusDays(28);
        LocalDateTime startOfPreviousWindow = startOfCurrentWindow.minusDays(28);

        long currentActiveDonors = donationRepository.countDistinctDonorByCreatedOnBetween(startOfCurrentWindow, endOfCurrentWindow);
        long previousActiveDonors = donationRepository.countDistinctDonorByCreatedOnBetween(startOfPreviousWindow, startOfCurrentWindow);

        return new MonthlyActiveDonorStatisticsDto(currentActiveDonors, previousActiveDonors);
    }
}
