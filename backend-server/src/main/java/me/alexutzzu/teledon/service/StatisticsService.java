package me.alexutzzu.teledon.service;

import me.alexutzzu.teledon.exception.NotFoundException;
import me.alexutzzu.teledon.model.dto.statistics.CharityStatisticsDto;
import me.alexutzzu.teledon.persistence.CharityRepository;
import me.alexutzzu.teledon.persistence.DonationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class StatisticsService {
    private final CharityRepository charityRepository;
    private final DonationRepository donationRepository;

    public StatisticsService(CharityRepository charityRepository, DonationRepository donationRepository) {
        this.charityRepository = charityRepository;
        this.donationRepository = donationRepository;
    }

    public List<CharityStatisticsDto> getDonationStatisticsForCharitySince(Long charityId, LocalDateTime since) {
        if (charityRepository.findById(charityId).isEmpty()) throw new NotFoundException();

        return donationRepository.findMonthlyStats(charityId, since);
    }
}
