package me.alexutzzu.teledon.persistence.impl;

import me.alexutzzu.teledon.model.AuthUser;
import me.alexutzzu.teledon.persistence.AuthUserRepository;
import me.alexutzzu.teledon.persistence.database.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class JdbcAuthUserRepositoryImpl implements AuthUserRepository {

    private final DatabaseManager databaseManager;

    public JdbcAuthUserRepositoryImpl(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public AuthUser create(AuthUser data) throws SQLException {
        try (Connection connection = databaseManager.getConnection()) {

            PreparedStatement stmt = connection.prepareStatement("INSERT INTO authuser(username, password) VALUES (?, ?) RETURNING *");
            stmt.setString(1, data.username());
            stmt.setString(2, data.password());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new AuthUser(rs.getLong("id"), rs.getString("username"), rs.getString("password"));
                }
                throw new SQLException("Could not create volunteer, no ID obtained.");
            }
        }
    }

    @Override
    public Optional<AuthUser> findById(Long id) throws SQLException {
        try (Connection connection = databaseManager.getConnection()) {

            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM authuser WHERE id = ?");
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new AuthUser(rs.getLong("id"), rs.getString("username"), rs.getString("password")));
                }
                return Optional.empty();
            }
        }
    }

    @Override
    public Optional<AuthUser> update(AuthUser data) throws SQLException {
        try (Connection connection = databaseManager.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("UPDATE authuser SET username = COALESCE(?, username), password = COALESCE(?, password) WHERE id = ? RETURNING *");
            stmt.setString(1, data.username());
            stmt.setString(2, data.password());
            stmt.setLong(3, data.id());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new AuthUser(rs.getLong("id"), rs.getString("username"), rs.getString("password")));
                }
                return Optional.empty();
            }
        }
    }

    @Override
    public void deleteById(Long id) throws SQLException {
        try (Connection connection = databaseManager.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM authuser WHERE id = ?");
            stmt.setLong(1, id);

            stmt.executeUpdate();
        }
    }

    @Override
    public Optional<AuthUser> findByUsername(String username) throws SQLException {
        try (Connection connection = databaseManager.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM authuser WHERE username = ?");
            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new AuthUser(rs.getLong("id"), rs.getString("username"), rs.getString("password")));
                }
                return Optional.empty();
            }
        }
    }
}
