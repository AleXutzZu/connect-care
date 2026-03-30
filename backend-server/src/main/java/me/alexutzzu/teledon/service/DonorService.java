package me.alexutzzu.teledon.service;

import me.alexutzzu.teledon.exception.DatabaseException;
import me.alexutzzu.teledon.model.Donor;
import me.alexutzzu.teledon.persistence.DonorRepository;
import me.alexutzzu.teledon.protos.DonorProtos;
import me.alexutzzu.teledon.service.mapper.DonorDtoEntityMapper;

import java.sql.SQLException;
import java.util.List;

public class DonorService {
    private final DonorRepository donorRepository;
    private final DonorDtoEntityMapper donorDtoEntityMapper;


    public DonorService(DonorRepository donorRepository, DonorDtoEntityMapper donorDtoEntityMapper) {
        this.donorRepository = donorRepository;
        this.donorDtoEntityMapper = donorDtoEntityMapper;
    }

    public List<DonorProtos.DonorDto> getAllDonors() throws DatabaseException {
        try {
            var donors = donorRepository.findAll();

            return donors.stream().map(donorDtoEntityMapper::toDomain).toList();
        } catch (SQLException e) {
            throw new DatabaseException("Database error occurred.");
        }
    }

    public DonorProtos.DonorDto createDonor(String firstName, String lastName, String address, String phoneNumber) throws DatabaseException {
        try {
            var donor = donorRepository.create(new Donor(0L, firstName, lastName, address, phoneNumber));
            return donorDtoEntityMapper.toDomain(donor);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to create donor with name " + firstName + " " + lastName);
        }
    }
}
