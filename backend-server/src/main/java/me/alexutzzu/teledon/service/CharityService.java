package me.alexutzzu.teledon.service;

import me.alexutzzu.teledon.exception.NotFoundException;
import me.alexutzzu.teledon.model.Charity;
import me.alexutzzu.teledon.model.dto.CharityDto;
import me.alexutzzu.teledon.model.dto.CharityWithRaisedSum;
import me.alexutzzu.teledon.persistence.CharityRepository;
import me.alexutzzu.teledon.service.mapper.CharityDtoEntityMapper;
import me.alexutzzu.teledon.service.mapper.CharityWithRaisedSumEntityMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CharityService {

    private final CharityRepository charityRepository;
    private final CharityWithRaisedSumEntityMapper charityWithRaisedSumEntityMapper;
    private final CharityDtoEntityMapper charityDtoEntityMapper;

    public CharityService(CharityRepository charityRepository, CharityWithRaisedSumEntityMapper charityWithRaisedSumEntityMapper, CharityDtoEntityMapper charityDtoEntityMapper) {
        this.charityRepository = charityRepository;
        this.charityWithRaisedSumEntityMapper = charityWithRaisedSumEntityMapper;
        this.charityDtoEntityMapper = charityDtoEntityMapper;
    }

    public List<CharityWithRaisedSum> getAllCharities() {
        return charityRepository.findAll().stream().map(charityWithRaisedSumEntityMapper::toDomain).toList();
    }

    public CharityDto getCharity(Long id) {
        return charityRepository.findById(id).map(charityDtoEntityMapper::toDomain).orElseThrow(NotFoundException::new);
    }

    public CharityDto createCharity(String name) {
        var entity = charityRepository.save(Charity.ofName(name));
        return charityDtoEntityMapper.toDomain(entity);
    }

    public CharityDto updateCharity(Long id, String name) {
        return charityRepository.findById(id)
                .map(charity -> {
                    charity.setName(name);
                    return charityRepository.save(charity);
                }).map(charityDtoEntityMapper::toDomain)
                .orElseThrow(NotFoundException::new);
    }

    public void deleteCharity(Long id) {
        if (charityRepository.findById(id).isEmpty()) throw new NotFoundException();
        charityRepository.deleteById(id);
    }
}
