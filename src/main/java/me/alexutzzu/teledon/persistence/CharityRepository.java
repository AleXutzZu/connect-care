package me.alexutzzu.teledon.persistence;

import me.alexutzzu.teledon.model.Charity;
import me.alexutzzu.teledon.persistence.database.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class CharityRepository implements BasicRepository<Charity> {

    private final DatabaseManager databaseManager;

    public CharityRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public Charity create(Charity data) throws SQLException {
        try (Connection connection = databaseManager.getConnection()) {

            PreparedStatement stmt = connection.prepareStatement("INSERT INTO charity(name) VALUES (?) RETURNING *");
            stmt.setString(1, data.name());
            try (ResultSet rs = stmt.executeQuery()) {
                return new Charity(rs.getLong("id"), rs.getString("name"));
            }
        }
    }

    @Override
    public Optional<Charity> findById(Long id) throws SQLException {
        try (Connection connection = databaseManager.getConnection()) {

            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM charity WHERE id = ?");
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Charity(rs.getLong("id"), rs.getString("name")));
                }
                return Optional.empty();
            }
        }
    }

    @Override
    public Optional<Charity> update(Charity data) throws SQLException {
        try (Connection connection = databaseManager.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("UPDATE charity SET name = COALESCE(?, name) WHERE id = ? RETURNING *");
            stmt.setString(1, data.name());
            stmt.setLong(2, data.id());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Charity(rs.getLong("id"), rs.getString("name")));
                }
                return Optional.empty();
            }
        }
    }

    @Override
    public void deleteById(Long id) throws SQLException {
        try (Connection connection = databaseManager.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM charity WHERE id = ?");
            stmt.setLong(1, id);

            stmt.executeUpdate();
        }
    }
}
