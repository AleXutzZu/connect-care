package me.alexutzzu.teledon.service;

import jakarta.transaction.Transactional;
import me.alexutzzu.teledon.model.Donor;
import me.alexutzzu.teledon.persistence.DonorRepository;
import me.alexutzzu.teledon.protos.DonorProtos;
import me.alexutzzu.teledon.service.mapper.DonorDtoEntityMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DonorService {
    private final DonorRepository donorRepository;
    private final DonorDtoEntityMapper donorDtoEntityMapper;


    public DonorService(DonorRepository donorRepository, DonorDtoEntityMapper donorDtoEntityMapper) {
        this.donorRepository = donorRepository;
        this.donorDtoEntityMapper = donorDtoEntityMapper;
    }

    public List<DonorProtos.DonorDto> getAllDonors() {
        var donors = donorRepository.findAll();
        return donors.stream().map(donorDtoEntityMapper::toDomain).toList();
    }

    public Optional<DonorProtos.DonorDto> getDonor(long id) {
        var donor = donorRepository.findById(id);

        return donor.map(donorDtoEntityMapper::toDomain);
    }

    @Transactional
    public DonorProtos.DonorDto createDonor(String firstName, String lastName, String address, String phoneNumber) {
        var donor = donorRepository.save(Donor.ofDetails(firstName, lastName, address, phoneNumber));
        return donorDtoEntityMapper.toDomain(donor);
    }
}
