package me.alexutzzu.teledon.service.mapper;

import me.alexutzzu.teledon.model.Donation;
import me.alexutzzu.teledon.model.dto.DonationDto;
import org.springframework.stereotype.Component;

@Component
public class DonationEntityMapper implements EntityMapper<Donation, DonationDto> {
    @Override
    public DonationDto toDomain(Donation entity) {
        return new DonationDto(entity.getId(), entity.getCharity().getId(), entity.getDonor().getId(), entity.getAmount());
    }
}
