package me.alexutzzu.teledon.service;

import jakarta.transaction.Transactional;
import me.alexutzzu.teledon.model.Charity;
import me.alexutzzu.teledon.model.dto.CharityDto;
import me.alexutzzu.teledon.persistence.CharityRepository;
import me.alexutzzu.teledon.persistence.DonationRepository;
import me.alexutzzu.teledon.protos.CharityProtos;
import me.alexutzzu.teledon.service.mapper.CharityDtoEntityMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CharityService {

    private final CharityRepository charityRepository;
    private final DonationRepository donationRepository;
    private final CharityDtoEntityMapper charityDtoEntityMapper;

    public CharityService(CharityRepository charityRepository, DonationRepository donationRepository, CharityDtoEntityMapper charityDtoEntityMapper) {
        this.charityRepository = charityRepository;
        this.donationRepository = donationRepository;
        this.charityDtoEntityMapper = charityDtoEntityMapper;
    }

    public List<CharityProtos.CharityDto> getAllCharities() {
        var charities = charityRepository.findAll();
        return charities.stream().map(c -> new CharityDto(c.getId(), c.getName(), donationRepository.findRaisedSum(c.getId()))).map(charityDtoEntityMapper::toDomain).toList();
    }

    public Optional<CharityProtos.CharityDto> getCharity(long id) {
        var charity = charityRepository.findById(id);

        if (charity.isEmpty()) return Optional.empty();

        var raisedSum = donationRepository.findRaisedSum(id);

        return Optional.of(charityDtoEntityMapper.toDomain(new CharityDto(charity.get().getId(), charity.get().getName(), raisedSum)));

    }

    @Transactional
    public CharityProtos.CharityDto createCharity(String name) {
        var charity = charityRepository.save(Charity.ofName(name));
        return charityDtoEntityMapper.toDomain(new CharityDto(charity.getId(), charity.getName(), 0));
    }
}
