package me.alexutzzu.teledon.controller;

import me.alexutzzu.teledon.service.DonationService;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/donations")
public class DonationController {
    private final DonationService donationService;

    public DonationController(DonationService donationService) {
        this.donationService = donationService;
    }

}
