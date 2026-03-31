package me.alexutzzu.teledon.service;

import me.alexutzzu.teledon.exception.DatabaseException;
import me.alexutzzu.teledon.model.Donation;
import me.alexutzzu.teledon.persistence.CharityRepository;
import me.alexutzzu.teledon.persistence.DonationRepository;
import me.alexutzzu.teledon.persistence.DonorRepository;
import me.alexutzzu.teledon.protos.DonationProtos;
import me.alexutzzu.teledon.service.mapper.DonationEntityMapper;

import java.sql.SQLException;

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

    public DonationProtos.Donation createDonation(Long charityId, Long donorId, double amount) throws DatabaseException {
        try {
            var charity = charityRepository.findById(charityId);

            if (charity.isEmpty()) {
                throw new DatabaseException("Charity with id " + charityId + " does not exist.");
            }

            var donor = donorRepository.findById(donorId);

            if (donor.isEmpty()) {
                throw new DatabaseException("Donor with id " + donorId + " does not exist.");
            }

            var donation = donationRepository.create(new Donation(0L, charity.get(), donor.get(), amount));
            return donationDtoEntityMapper.toDomain(donation);

        } catch (SQLException e) {
            throw new DatabaseException("Database error occurred.");
        }
    }
}
