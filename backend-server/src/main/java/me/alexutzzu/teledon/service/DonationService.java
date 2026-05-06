package me.alexutzzu.teledon.service;

import me.alexutzzu.teledon.exception.NotFoundException;
import me.alexutzzu.teledon.model.Charity;
import me.alexutzzu.teledon.model.Donation;
import me.alexutzzu.teledon.model.Donor;
import me.alexutzzu.teledon.model.dto.DonationDto;
import me.alexutzzu.teledon.persistence.CharityRepository;
import me.alexutzzu.teledon.persistence.DonationRepository;
import me.alexutzzu.teledon.persistence.DonorRepository;
import me.alexutzzu.teledon.service.mapper.DonationDtoEntityMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DonationService {
    private final DonationRepository donationRepository;
    private final DonorRepository donorRepository;
    private final CharityRepository charityRepository;
    private final DonationDtoEntityMapper donationDtoEntityMapper;

    public DonationService(DonationRepository donationRepository, DonorRepository donorRepository, CharityRepository charityRepository, DonationDtoEntityMapper donationDtoEntityMapper) {
        this.donationRepository = donationRepository;
        this.donorRepository = donorRepository;
        this.charityRepository = charityRepository;
        this.donationDtoEntityMapper = donationDtoEntityMapper;
    }

    public List<DonationDto> getAllDonations() {
        return donationRepository.findAll().stream().map(donationDtoEntityMapper::toDomain).toList();
    }

    public DonationDto getDonation(Long id) {
        return donationRepository.findById(id).map(donationDtoEntityMapper::toDomain).orElseThrow(NotFoundException::new);
    }

    public DonationDto createDonation(Long charityId, Long donorId, Double amount) {
        Charity charity = charityRepository.findById(charityId).orElseThrow(NotFoundException::new);
        Donor donor = donorRepository.findById(donorId).orElseThrow(NotFoundException::new);

        Donation donation = Donation.ofDetails(charity, donor, amount);
        var entity = donationRepository.save(donation);
        return donationDtoEntityMapper.toDomain(entity);
    }

    public DonationDto updateDonation(Long id, Long charityId, Long donorId, Double amount) {
        Charity charity = charityRepository.findById(charityId).orElseThrow(NotFoundException::new);
        Donor donor = donorRepository.findById(donorId).orElseThrow(NotFoundException::new);


        return donationRepository.findById(id).map(donation -> {
            donation.setCharity(charity);
            donation.setDonor(donor);
            donation.setAmount(amount);
            return donationRepository.save(donation);
        }).map(donationDtoEntityMapper::toDomain).orElseThrow(NotFoundException::new);
    }

    public void deleteDonation(Long id) {
        if (donationRepository.findById(id).isEmpty()) throw new NotFoundException();

        donationRepository.deleteById(id);
    }
}
