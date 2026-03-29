package me.alexutzzu.teledon.service.mapper;

public interface EntityMapper<D, E> {
    D toDomain(E entity);

    E toEntity(D domain);
}
