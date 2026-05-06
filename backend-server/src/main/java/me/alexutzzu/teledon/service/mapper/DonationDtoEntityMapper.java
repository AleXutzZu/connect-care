package me.alexutzzu.teledon.service.mapper;

import me.alexutzzu.teledon.model.Donation;
import me.alexutzzu.teledon.model.dto.DonationDto;
import org.springframework.stereotype.Component;

@Component
public class DonationDtoEntityMapper implements EntityMapper<Donation, DonationDto> {
    @Override
    public DonationDto toDomain(Donation donation) {
        return new DonationDto(
                donation.getId(),
                donation.getAmount(),
                donation.getDonor().getId(),
                donation.getDonor().getFirstName(),
                donation.getDonor().getLastName(),
                donation.getCharity().getId(),
                donation.getCharity().getName(),
                donation.getCreatedOn()
        );
    }
}
