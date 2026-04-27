package me.alexutzzu.teledon.service.mapper;

import me.alexutzzu.teledon.model.Donation;
import me.alexutzzu.teledon.protos.CharityProtos;
import me.alexutzzu.teledon.protos.DonationProtos;
import me.alexutzzu.teledon.protos.DonorProtos;
import org.springframework.stereotype.Component;

@Component
public class DonationEntityMapper implements EntityMapper<Donation, DonationProtos.Donation> {

    @Override
    public DonationProtos.Donation toDomain(Donation entity) {
        return DonationProtos.Donation.newBuilder()
                .setCharity(
                        CharityProtos.Charity.newBuilder()
                                .setId(entity.getCharity().getId())
                                .setName(entity.getCharity().getName())
                                .build()
                )
                .setAmount(entity.getAmount())
                .setDonor(
                        DonorProtos.DonorDto.newBuilder()
                                .setId(entity.getDonor().getId())
                                .setFirstName(entity.getDonor().getFirstName())
                                .setAddress(entity.getDonor().getAddress())
                                .setLastName(entity.getDonor().getLastName())
                                .setPhoneNumber(entity.getDonor().getPhoneNumber())
                                .build()
                )
                .setId(entity.getId())
                .build();
    }

}
