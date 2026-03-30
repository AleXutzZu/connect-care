package me.alexutzzu.teledon.service.mapper;

import me.alexutzzu.teledon.model.Donor;
import me.alexutzzu.teledon.protos.DonorProtos;

public class DonorDtoEntityMapper implements EntityMapper<DonorProtos.DonorDto, Donor> {
    @Override
    public DonorProtos.DonorDto toDomain(Donor entity) {
        return DonorProtos.DonorDto.newBuilder()
                .setId(entity.id())
                .setFirstName(entity.firstName())
                .setLastName(entity.lastName())
                .setAddress(entity.address())
                .setPhoneNumber(entity.phoneNumber())
                .build();
    }

    @Override
    public Donor toEntity(DonorProtos.DonorDto domain) {
        return new Donor(domain.getId(), domain.getFirstName(), domain.getLastName(), domain.getAddress(), domain.getPhoneNumber());
    }
}
