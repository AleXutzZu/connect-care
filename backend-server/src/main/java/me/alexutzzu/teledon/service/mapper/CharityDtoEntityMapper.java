package me.alexutzzu.teledon.service.mapper;

import me.alexutzzu.teledon.model.Charity;
import me.alexutzzu.teledon.model.dto.CharityDto;
import me.alexutzzu.teledon.model.dto.DonationDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CharityDtoEntityMapper implements EntityMapper<Charity, CharityDto> {

    private final DonationDtoEntityMapper donationDtoEntityMapper;

    public CharityDtoEntityMapper(DonationDtoEntityMapper donationDtoEntityMapper) {
        this.donationDtoEntityMapper = donationDtoEntityMapper;
    }

    @Override
    public CharityDto toDomain(Charity entity) {
        List<DonationDto> donationDtos = entity.getDonations().stream().map(donationDtoEntityMapper::toDomain).toList();
        return new CharityDto(entity.getId(), entity.getName(), entity.getUser().getUsername(), entity.getTarget(), entity.getCause(), donationDtos);
    }
}
