package me.alexutzzu.teledon.service.mapper;

import me.alexutzzu.teledon.model.Charity;
import me.alexutzzu.teledon.model.Donation;
import me.alexutzzu.teledon.model.Donor;
import me.alexutzzu.teledon.protos.CharityProtos;
import me.alexutzzu.teledon.protos.DonationProtos;
import me.alexutzzu.teledon.protos.DonorProtos;

public class DonationEntityMapper implements EntityMapper<DonationProtos.Donation, Donation> {

    @Override
    public DonationProtos.Donation toDomain(Donation entity) {
        return DonationProtos.Donation.newBuilder()
                .setCharity(
                        CharityProtos.Charity.newBuilder()
                                .setId(entity.charity().id())
                                .setName(entity.charity().name())
                                .build()
                )
                .setAmount(entity.amount())
                .setDonor(
                        DonorProtos.DonorDto.newBuilder()
                                .setId(entity.donor().id())
                                .setFirstName(entity.donor().firstName())
                                .setAddress(entity.donor().address())
                                .setLastName(entity.donor().lastName())
                                .setPhoneNumber(entity.donor().phoneNumber())
                                .build()
                )
                .setId(entity.id())
                .build();
    }

    @Override
    public Donation toEntity(DonationProtos.Donation domain) {
        return new Donation(
                domain.getId(),
                new Charity(domain.getCharity().getId(), domain.getCharity().getName()),
                new Donor(domain.getDonor().getId(), domain.getDonor().getFirstName(), domain.getDonor().getLastName(), domain.getDonor().getAddress(), domain.getDonor().getPhoneNumber()),
                domain.getAmount()
        );
    }
}
