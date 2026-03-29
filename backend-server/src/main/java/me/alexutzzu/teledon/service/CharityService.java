package me.alexutzzu.teledon.service;

import me.alexutzzu.teledon.exception.DatabaseException;
import me.alexutzzu.teledon.model.Charity;
import me.alexutzzu.teledon.model.dto.CharityDto;
import me.alexutzzu.teledon.persistence.CharityRepository;
import me.alexutzzu.teledon.persistence.DonationRepository;
import me.alexutzzu.teledon.protos.CharityProtos;
import me.alexutzzu.teledon.service.mapper.CharityDtoEntityMapper;

import java.sql.SQLException;
import java.util.List;

public class CharityService {

    private final CharityRepository charityRepository;
    private final DonationRepository donationRepository;
    private final CharityDtoEntityMapper charityDtoEntityMapper;

    public CharityService(CharityRepository charityRepository, DonationRepository donationRepository, CharityDtoEntityMapper charityDtoEntityMapper) {
        this.charityRepository = charityRepository;
        this.donationRepository = donationRepository;
        this.charityDtoEntityMapper = charityDtoEntityMapper;
    }

    public List<CharityProtos.CharityDto> getAllCharities() throws DatabaseException {
        try {
            var charities = charityRepository.findAll();

            return charities.stream().map(c -> {
                double sum = 0;
                try {
                    sum = donationRepository.findRaisedSum(c.id());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                return new CharityDto(c.id(), c.name(), sum);
            }).map(charityDtoEntityMapper::toEntity).toList();

        } catch (SQLException | RuntimeException e) {
            throw new DatabaseException("Database error occurred.");
        }
    }

    public CharityProtos.CharityDto createCharity(String name) throws DatabaseException {
        try {
            var charity = charityRepository.create(new Charity(0L, name));
            return charityDtoEntityMapper.toEntity(new CharityDto(charity.id(), charity.name(), 0));
        } catch (SQLException e) {
            throw new DatabaseException("Failed to create charity with name " + name);
        }
    }
}
