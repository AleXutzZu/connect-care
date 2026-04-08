package me.alexutzzu.teledon.persistence;

import me.alexutzzu.teledon.model.Charity;

import java.sql.SQLException;
import java.util.List;

public interface CharityRepository extends BasicRepository<Charity> {
    List<Charity> findAll() throws SQLException;
}
