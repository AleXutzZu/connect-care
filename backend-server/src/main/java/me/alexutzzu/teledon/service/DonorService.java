package me.alexutzzu.teledon.service;

import me.alexutzzu.teledon.exception.NotFoundException;
import me.alexutzzu.teledon.model.Donor;
import me.alexutzzu.teledon.model.dto.DonorDto;
import me.alexutzzu.teledon.model.dto.DonorWithoutDonations;
import me.alexutzzu.teledon.persistence.DonorRepository;
import me.alexutzzu.teledon.service.mapper.DonorDtoEntityMapper;
import me.alexutzzu.teledon.service.mapper.DonorWithoutDonationsEntityMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DonorService {
    private final DonorRepository donorRepository;
    private final DonorDtoEntityMapper donorDtoEntityMapper;
    private final DonorWithoutDonationsEntityMapper donorWithoutDonationsEntityMapper;

    public DonorService(DonorRepository donorRepository, DonorDtoEntityMapper donorDtoEntityMapper, DonorWithoutDonationsEntityMapper donorWithoutDonationsEntityMapper) {
        this.donorRepository = donorRepository;
        this.donorDtoEntityMapper = donorDtoEntityMapper;
        this.donorWithoutDonationsEntityMapper = donorWithoutDonationsEntityMapper;
    }

    public List<DonorWithoutDonations> getAllDonors() {
        return donorRepository.findAll().stream().map(donorWithoutDonationsEntityMapper::toDomain).toList();
    }

    public DonorDto getDonor(Long id) {
        return donorRepository.findById(id).map(donorDtoEntityMapper::toDomain).orElseThrow(NotFoundException::new);
    }

    public DonorDto createDonor(String firstName, String lastName, String address, String phoneNumber) {
        Donor entity = donorRepository.save(Donor.ofDetails(firstName, lastName, address, phoneNumber));
        return donorDtoEntityMapper.toDomain(entity);
    }

    public DonorDto updateDonor(Long id, String firstName, String lastName, String address, String phoneNumber) {
        return donorRepository.findById(id).map(donor -> {
            donor.setFirstName(firstName);
            donor.setLastName(lastName);
            donor.setAddress(address);
            donor.setPhoneNumber(phoneNumber);
            return donorRepository.save(donor);
        }).map(donorDtoEntityMapper::toDomain).orElseThrow(NotFoundException::new);
    }

    public void deleteDonor(Long id) {
        if (donorRepository.findById(id).isEmpty()) throw new NotFoundException();
        donorRepository.deleteById(id);
    }
}
