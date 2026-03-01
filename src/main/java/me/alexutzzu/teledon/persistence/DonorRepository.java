package me.alexutzzu.teledon.persistence;

import me.alexutzzu.teledon.model.Donor;
import me.alexutzzu.teledon.persistence.database.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class DonorRepository implements BasicRepository<Donor> {

    private final DatabaseManager databaseManager;

    public DonorRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public Donor create(Donor data) throws SQLException {
        try (Connection connection = databaseManager.getConnection()) {

            PreparedStatement stmt = connection.prepareStatement("INSERT INTO donor(firstName, lastName, address, phoneNumber) VALUES (?, ?, ?, ?) RETURNING *");
            stmt.setString(1, data.firstName());
            stmt.setString(2, data.lastName());
            stmt.setString(3, data.address());
            stmt.setString(4, data.phoneNumber());
            try (ResultSet rs = stmt.executeQuery()) {
                return new Donor(rs.getLong("id"), rs.getString("firstName"), rs.getString("lastName"), rs.getString("address"), rs.getString("phoneNumber"));
            }
        }
    }

    @Override
    public Optional<Donor> findById(Long id) throws SQLException {
        try (Connection connection = databaseManager.getConnection()) {

            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM donor WHERE id = ?");
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Donor(rs.getLong("id"), rs.getString("firstName"), rs.getString("lastName"), rs.getString("address"), rs.getString("phoneNumber")));
                }
                return Optional.empty();
            }
        }
    }

    @Override
    public Optional<Donor> update(Donor data) throws SQLException {
        try (Connection connection = databaseManager.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("UPDATE donor SET firstName = COALESCE(?, firstName), lastName = COALESCE(?, lastName), address = COALESCE(?, address), phoneNumber = COALESCE(?, phoneNumber) WHERE id = ? RETURNING *");
            stmt.setString(1, data.firstName());
            stmt.setString(2, data.lastName());
            stmt.setString(3, data.address());
            stmt.setString(4, data.phoneNumber());
            stmt.setLong(5, data.id());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Donor(rs.getLong("id"), rs.getString("firstName"), rs.getString("lastName"), rs.getString("address"), rs.getString("phoneNumber")));
                }
                return Optional.empty();
            }
        }
    }

    @Override
    public void deleteById(Long id) throws SQLException {
        try (Connection connection = databaseManager.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM donor WHERE id = ?");
            stmt.setLong(1, id);

            stmt.executeUpdate();
        }
    }
}
