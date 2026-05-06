package me.alexutzzu.teledon.service.mapper;

import me.alexutzzu.teledon.model.Donor;
import me.alexutzzu.teledon.model.dto.DonorWithoutDonations;
import org.springframework.stereotype.Component;

@Component
public class DonorWithoutDonationsEntityMapper implements EntityMapper<Donor, DonorWithoutDonations> {
    @Override
    public DonorWithoutDonations toDomain(Donor entity) {
        return new DonorWithoutDonations(entity.getId(), entity.getFirstName(), entity.getLastName(), entity.getAddress(), entity.getPhoneNumber());
    }
}
