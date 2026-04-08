package me.alexutzzu.teledon.persistence;

import me.alexutzzu.teledon.model.Donation;

import java.sql.SQLException;
import java.util.List;

public interface DonationRepository extends BasicRepository<Donation> {

    List<Donation> findAllByCharityId(long charityId) throws SQLException;

    double findRaisedSum(long charityId) throws SQLException;
}
