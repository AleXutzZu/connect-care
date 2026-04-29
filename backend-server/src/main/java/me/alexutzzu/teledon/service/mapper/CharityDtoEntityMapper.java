package me.alexutzzu.teledon.service.mapper;

import me.alexutzzu.teledon.model.dto.CharityDto;
import me.alexutzzu.teledon.protos.CharityProtos;
import org.springframework.stereotype.Component;

@Component
public class CharityDtoEntityMapper implements EntityMapper<CharityDto, CharityProtos.CharityDto> {


    @Override
    public CharityProtos.CharityDto toDomain(CharityDto entity) {
        return CharityProtos.CharityDto.newBuilder()
                .setRaisedSum(entity.raisedSum())
                .setName(entity.name())
                .setId(entity.id())
                .build();
    }
}
