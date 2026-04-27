package me.alexutzzu.teledon.service;

import jakarta.transaction.Transactional;
import me.alexutzzu.teledon.model.Donation;
import me.alexutzzu.teledon.persistence.CharityRepository;
import me.alexutzzu.teledon.persistence.DonationRepository;
import me.alexutzzu.teledon.persistence.DonorRepository;
import me.alexutzzu.teledon.protos.DonationProtos;
import me.alexutzzu.teledon.service.mapper.DonationEntityMapper;
import org.springframework.stereotype.Service;

@Service
public class DonationService {
    private final DonationRepository donationRepository;
    private final DonorRepository donorRepository;
    private final CharityRepository charityRepository;

    private final DonationEntityMapper donationDtoEntityMapper;

    public DonationService(DonationRepository donationRepository, DonorRepository donorRepository, CharityRepository charityRepository, DonationEntityMapper donationDtoEntityMapper) {
        this.donationRepository = donationRepository;
        this.donorRepository = donorRepository;
        this.charityRepository = charityRepository;
        this.donationDtoEntityMapper = donationDtoEntityMapper;
    }

    @Transactional
    public DonationProtos.Donation createDonation(Long charityId, Long donorId, double amount) {
        var charity = charityRepository.getReferenceById(charityId);
        var donor = donorRepository.getReferenceById(donorId);

        var donation = donationRepository.save(Donation.ofCharity(charity, donor, amount));
        return donationDtoEntityMapper.toDomain(donation);
    }
}
