package me.alexutzzu.teledon.service.mapper;

import me.alexutzzu.teledon.model.Charity;
import me.alexutzzu.teledon.model.Donation;
import me.alexutzzu.teledon.model.Donor;
import me.alexutzzu.teledon.protos.CharityProtos;
import me.alexutzzu.teledon.protos.DonationProtos;
import me.alexutzzu.teledon.protos.DonorProtos;

public class DonationEntityMapper implements EntityMapper<Donation, DonationProtos.Donation> {

    @Override
    public DonationProtos.Donation toEntity(Donation domain) {
        return DonationProtos.Donation.newBuilder()
                .setCharity(
                        CharityProtos.Charity.newBuilder()
                                .setId(domain.charity().id())
                                .setName(domain.charity().name())
                                .build()
                )
                .setAmount(domain.amount())
                .setDonor(
                        DonorProtos.DonorDto.newBuilder()
                                .setId(domain.donor().id())
                                .setFirstName(domain.donor().firstName())
                                .setAddress(domain.donor().address())
                                .setLastName(domain.donor().lastName())
                                .setPhoneNumber(domain.donor().phoneNumber())
                                .build()
                )
                .setId(domain.id())
                .build();
    }

    @Override
    public Donation toDomain(DonationProtos.Donation entity) {
        return new Donation(
                entity.getId(),
                new Charity(entity.getCharity().getId(), entity.getCharity().getName()),
                new Donor(entity.getDonor().getId(), entity.getDonor().getFirstName(), entity.getDonor().getLastName(), entity.getDonor().getAddress(), entity.getDonor().getPhoneNumber()),
                entity.getAmount()
        );
    }
}
