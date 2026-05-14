package me.alexutzzu.teledon.controller;

import me.alexutzzu.teledon.model.dto.statistics.*;
import me.alexutzzu.teledon.service.StatisticsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/charities/{charityId}")
    public List<CharityDonationsStatisticsDto> getDonationStatisticsOfCharity(@PathVariable Long charityId, @RequestParam("since") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate since) {
        return statisticsService.getDonationStatisticsForCharitySince(charityId, since.atStartOfDay());
    }


    @GetMapping
    public GeneralStatisticsDto getGeneralStatistics(@RequestParam("since") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate since) {
        MonthToDateDonorStatisticsDto monthToDateDonors = statisticsService.getMonthToDateDonorStatistics();
        MonthlyActiveDonorStatisticsDto monthlyActiveDonors = statisticsService.getMonthlyActiveDonorStatistics();
        List<DonationStatisticsDto> dailyDonations = statisticsService.getDailyDonationStats(since.atStartOfDay());
        return new GeneralStatisticsDto(monthToDateDonors, monthlyActiveDonors, dailyDonations);
    }
}
