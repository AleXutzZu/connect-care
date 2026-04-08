package me.alexutzzu.teledon.persistence.impl;

import me.alexutzzu.teledon.model.Charity;
import me.alexutzzu.teledon.model.Donation;
import me.alexutzzu.teledon.model.Donor;
import me.alexutzzu.teledon.persistence.DonationRepository;
import me.alexutzzu.teledon.persistence.database.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcDonationRepositoryImpl implements DonationRepository {

    private final DatabaseManager databaseManager;

    public JdbcDonationRepositoryImpl(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public Donation create(Donation data) throws SQLException {
        try (Connection connection = databaseManager.getConnection()) {

            PreparedStatement stmt = connection.prepareStatement("INSERT INTO donation(charityid, donorid, amount) VALUES (?, ?, ?) RETURNING id");
            stmt.setLong(1, data.charity().id());
            stmt.setLong(2, data.donor().id());
            stmt.setDouble(3, data.amount());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return findById(rs.getLong("id")).orElseThrow(() -> new SQLException("Failed to retrieve created donation."));
                }
                throw new SQLException("Failed to create donation, no ID obtained.");
            }
        }
    }

    @Override
    public Optional<Donation> findById(Long id) throws SQLException {
        try (Connection connection = databaseManager.getConnection()) {

            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT d.*, c.name AS charity_name, " +
                            "dn.firstName, dn.lastName, dn.address, dn.phoneNumber " +
                            "FROM donation d " +
                            "JOIN charity c ON c.id = d.charityid " +
                            "JOIN donor dn ON dn.id = d.donorid " +
                            "WHERE d.id = ?");
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Charity charity = new Charity(rs.getLong("charityid"), rs.getString("charity_name"));
                    Donor donor = new Donor(
                            rs.getLong("donorid"),
                            rs.getString("firstName"),
                            rs.getString("lastName"),
                            rs.getString("address"),
                            rs.getString("phoneNumber")
                    );
                    return Optional.of(new Donation(rs.getLong("id"), charity, donor, rs.getDouble("amount")));
                }
                return Optional.empty();
            }
        }
    }

    @Override
    public Optional<Donation> update(Donation data) throws SQLException {
        try (Connection connection = databaseManager.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE donation SET charityid = COALESCE(?, charityid), donorid = COALESCE(?, donorid), amount = COALESCE(?, amount) WHERE id = ? RETURNING id"
            );
            if (data.charity() != null && data.charity().id() != null) stmt.setLong(1, data.charity().id());
            else stmt.setNull(1, java.sql.Types.BIGINT);
            if (data.donor() != null && data.donor().id() != null) stmt.setLong(2, data.donor().id());
            else stmt.setNull(2, java.sql.Types.BIGINT);
            if (data.amount() != null) stmt.setDouble(3, data.amount());
            else stmt.setNull(3, java.sql.Types.DOUBLE);
            stmt.setLong(4, data.id());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return findById(rs.getLong("id"));
                }
                return Optional.empty();
            }
        }
    }

    @Override
    public void deleteById(Long id) throws SQLException {
        try (Connection connection = databaseManager.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM donation WHERE id = ?");
            stmt.setLong(1, id);

            stmt.executeUpdate();
        }
    }

    @Override
    public List<Donation> findAllByCharityId(long charityId) throws SQLException {
        try (Connection connection = databaseManager.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT d.*, c.name AS charity_name, " +
                    "dn.firstName, dn.lastName, dn.address, dn.phoneNumber " +
                    "FROM donation d " +
                    "JOIN charity c ON c.id = d.charityid " +
                    "JOIN donor dn ON dn.id = d.donorid " +
                    "WHERE d.id = ?");
            stmt.setLong(1, charityId);

            List<Donation> donations = new ArrayList<>();
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Charity charity = new Charity(rs.getLong("charityid"), rs.getString("charity_name"));
                    Donor donor = new Donor(
                            rs.getLong("donorid"),
                            rs.getString("firstName"),
                            rs.getString("lastName"),
                            rs.getString("address"),
                            rs.getString("phoneNumber")
                    );
                    donations.add(new Donation(rs.getLong("id"), charity, donor, rs.getDouble("amount")));
                }
                return donations;
            }
        }
    }

    @Override
    public double findRaisedSum(long charityId) throws SQLException {
        try (Connection connection = databaseManager.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT sum(amount) FROM donation WHERE charityid = ? GROUP BY charityid");
            stmt.setLong(1, charityId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
                return 0;
            }
        }
    }
}