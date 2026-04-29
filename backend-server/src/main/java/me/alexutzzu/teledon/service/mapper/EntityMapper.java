package me.alexutzzu.teledon.service.mapper;

public interface EntityMapper<E, D> {
    D toDomain(E entity);
}
