package me.alexutzzu.teledon.service.mapper;

import me.alexutzzu.teledon.model.dto.CharityDto;
import me.alexutzzu.teledon.protos.CharityProtos;

public class CharityDtoEntityMapper implements EntityMapper<CharityDto, CharityProtos.CharityDto> {
    @Override
    public CharityDto toDomain(CharityProtos.CharityDto entity) {
        return new CharityDto(entity.getId(), entity.getName(), entity.getRaisedSum());
    }

    @Override
    public CharityProtos.CharityDto toEntity(CharityDto domain) {
        return CharityProtos.CharityDto.newBuilder()
                .setRaisedSum(domain.raisedSum())
                .setName(domain.name())
                .setId(domain.id())
                .build();
    }
}
