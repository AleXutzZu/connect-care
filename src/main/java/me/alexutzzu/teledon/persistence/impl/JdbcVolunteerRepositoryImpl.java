package me.alexutzzu.teledon.persistence.impl;

import me.alexutzzu.teledon.model.Volunteer;
import me.alexutzzu.teledon.persistence.BasicRepository;
import me.alexutzzu.teledon.persistence.VolunteerRepository;
import me.alexutzzu.teledon.persistence.database.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class JdbcVolunteerRepositoryImpl implements VolunteerRepository {

    private final DatabaseManager databaseManager;

    public JdbcVolunteerRepositoryImpl(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public Volunteer create(Volunteer data) throws SQLException {
        try (Connection connection = databaseManager.getConnection()) {

            PreparedStatement stmt = connection.prepareStatement("INSERT INTO volunteer(username, password) VALUES (?, ?) RETURNING *");
            stmt.setString(1, data.username());
            stmt.setString(2, data.password());
            try (ResultSet rs = stmt.executeQuery()) {
                return new Volunteer(rs.getLong("id"), rs.getString("username"), rs.getString("password"));
            }
        }
    }

    @Override
    public Optional<Volunteer> findById(Long id) throws SQLException {
        try (Connection connection = databaseManager.getConnection()) {

            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM volunteer WHERE id = ?");
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Volunteer(rs.getLong("id"), rs.getString("username"), rs.getString("password")));
                }
                return Optional.empty();
            }
        }
    }

    @Override
    public Optional<Volunteer> update(Volunteer data) throws SQLException {
        try (Connection connection = databaseManager.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("UPDATE volunteer SET username = COALESCE(?, username), password = COALESCE(?, password) WHERE id = ? RETURNING *");
            stmt.setString(1, data.username());
            stmt.setString(2, data.password());
            stmt.setLong(3, data.id());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Volunteer(rs.getLong("id"), rs.getString("username"), rs.getString("password")));
                }
                return Optional.empty();
            }
        }
    }

    @Override
    public void deleteById(Long id) throws SQLException {
        try (Connection connection = databaseManager.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM volunteer WHERE id = ?");
            stmt.setLong(1, id);

            stmt.executeUpdate();
        }
    }
}
