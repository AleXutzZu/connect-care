package me.alexutzzu.teledon.service.mapper;

import me.alexutzzu.teledon.model.Donor;
import me.alexutzzu.teledon.protos.DonorProtos;
import org.springframework.stereotype.Component;

@Component
public class DonorDtoEntityMapper implements EntityMapper<Donor, DonorProtos.DonorDto> {
    @Override
    public DonorProtos.DonorDto toDomain(Donor entity) {
        return DonorProtos.DonorDto.newBuilder()
                .setId(entity.getId())
                .setFirstName(entity.getFirstName())
                .setLastName(entity.getLastName())
                .setAddress(entity.getAddress())
                .setPhoneNumber(entity.getPhoneNumber())
                .build();
    }
}
