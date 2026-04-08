package me.alexutzzu.teledon.service.mapper;

import me.alexutzzu.teledon.model.Donor;
import me.alexutzzu.teledon.protos.DonorProtos;

public class DonorDtoEntityMapper implements EntityMapper<Donor, DonorProtos.DonorDto> {
    @Override
    public DonorProtos.DonorDto toEntity(Donor domain) {
        return DonorProtos.DonorDto.newBuilder()
                .setId(domain.id())
                .setFirstName(domain.firstName())
                .setLastName(domain.lastName())
                .setAddress(domain.address())
                .setPhoneNumber(domain.phoneNumber())
                .build();
    }

    @Override
    public Donor toDomain(DonorProtos.DonorDto entity) {
        return new Donor(entity.getId(), entity.getFirstName(), entity.getLastName(), entity.getAddress(), entity.getPhoneNumber());
    }
}
