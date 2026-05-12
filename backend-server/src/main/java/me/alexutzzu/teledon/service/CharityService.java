package me.alexutzzu.teledon.service;

import me.alexutzzu.teledon.controller.dto.CreateCharityRequest;
import me.alexutzzu.teledon.exception.NotFoundException;
import me.alexutzzu.teledon.model.Charity;
import me.alexutzzu.teledon.model.User;
import me.alexutzzu.teledon.model.dto.CharityDto;
import me.alexutzzu.teledon.model.dto.CharityWithRaisedSum;
import me.alexutzzu.teledon.persistence.CharityRepository;
import me.alexutzzu.teledon.persistence.UserRepository;
import me.alexutzzu.teledon.service.mapper.CharityDtoEntityMapper;
import me.alexutzzu.teledon.service.mapper.CharityWithRaisedSumEntityMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class CharityService {

    private final CharityRepository charityRepository;
    private final UserRepository userRepository;
    private final CharityWithRaisedSumEntityMapper charityWithRaisedSumEntityMapper;
    private final CharityDtoEntityMapper charityDtoEntityMapper;

    public CharityService(CharityRepository charityRepository, UserRepository userRepository, CharityWithRaisedSumEntityMapper charityWithRaisedSumEntityMapper, CharityDtoEntityMapper charityDtoEntityMapper) {
        this.charityRepository = charityRepository;
        this.userRepository = userRepository;
        this.charityWithRaisedSumEntityMapper = charityWithRaisedSumEntityMapper;
        this.charityDtoEntityMapper = charityDtoEntityMapper;
    }

    public Page<CharityWithRaisedSum> getAllCharities(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        if (search == null || search.isBlank()) {
            return charityRepository.findAll(pageable)
                    .map(charityWithRaisedSumEntityMapper::toDomain);
        }
        return charityRepository.findByNameContainingIgnoreCase(search, pageable)
                .map(charityWithRaisedSumEntityMapper::toDomain);
    }

    public CharityDto getCharity(Long id) {
        return charityRepository.findById(id).map(charityDtoEntityMapper::toDomain).orElseThrow(NotFoundException::new);
    }

    public CharityDto createCharity(CreateCharityRequest createCharityRequest, String username) {
        User user = userRepository.findByUsername(username).orElseThrow(NotFoundException::new);
        var entity = charityRepository.save(Charity.of(createCharityRequest.name(), user, createCharityRequest.target(), createCharityRequest.cause()));
        return charityDtoEntityMapper.toDomain(entity);
    }

    public CharityDto updateCharity(Long id, CreateCharityRequest createCharityRequest) {
        return charityRepository.findById(id)
                .map(charity -> {
                    charity.setName(createCharityRequest.name());
                    charity.setTarget(createCharityRequest.target());
                    charity.setCause(createCharityRequest.cause());
                    return charityRepository.save(charity);
                }).map(charityDtoEntityMapper::toDomain)
                .orElseThrow(NotFoundException::new);
    }

    public void deleteCharity(Long id) {
        if (charityRepository.findById(id).isEmpty()) throw new NotFoundException();
        charityRepository.deleteById(id);
    }
}
