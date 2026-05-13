package me.alexutzzu.teledon.service.mapper;

import me.alexutzzu.teledon.model.Donor;
import me.alexutzzu.teledon.model.dto.DonorDto;
import org.springframework.stereotype.Component;

@Component
public class DonorDtoEntityMapper implements EntityMapper<Donor, DonorDto> {
    private final DonationDtoEntityMapper donationDtoEntityMapper;

    public DonorDtoEntityMapper(DonationDtoEntityMapper donationDtoEntityMapper) {
        this.donationDtoEntityMapper = donationDtoEntityMapper;
    }

    @Override
    public DonorDto toDomain(Donor entity) {
        var donations = entity.getDonations().stream().map(donationDtoEntityMapper::toDomain).toList();
        return new DonorDto(entity.getId(), entity.getFirstName(), entity.getLastName(), entity.getAddress(), entity.getPhoneNumber(), donations, entity.getCreatedOn());
    }
}
