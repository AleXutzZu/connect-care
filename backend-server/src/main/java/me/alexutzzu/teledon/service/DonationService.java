package me.alexutzzu.teledon.service;

import me.alexutzzu.teledon.persistence.CharityRepository;
import me.alexutzzu.teledon.persistence.DonationRepository;
import me.alexutzzu.teledon.persistence.DonorRepository;
import org.springframework.stereotype.Service;

@Service
public class DonationService {
    private final DonationRepository donationRepository;
    private final DonorRepository donorRepository;
    private final CharityRepository charityRepository;


    public DonationService(DonationRepository donationRepository, DonorRepository donorRepository, CharityRepository charityRepository) {
        this.donationRepository = donationRepository;
        this.donorRepository = donorRepository;
        this.charityRepository = charityRepository;
    }

}
