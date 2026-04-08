package me.alexutzzu.teledon.persistence;

import me.alexutzzu.teledon.model.Donor;

import java.sql.SQLException;
import java.util.List;

public interface DonorRepository extends BasicRepository<Donor> {

    List<Donor> findAll() throws SQLException;
}
