package me.alexutzzu.teledon.service;

import me.alexutzzu.teledon.persistence.CharityRepository;
import me.alexutzzu.teledon.persistence.DonationRepository;
import org.springframework.stereotype.Service;

@Service
public class CharityService {

    private final CharityRepository charityRepository;
    private final DonationRepository donationRepository;

    public CharityService(CharityRepository charityRepository, DonationRepository donationRepository) {
        this.charityRepository = charityRepository;
        this.donationRepository = donationRepository;
    }

}
