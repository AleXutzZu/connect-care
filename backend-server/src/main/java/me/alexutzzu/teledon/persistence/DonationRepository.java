package me.alexutzzu.teledon.persistence;

import me.alexutzzu.teledon.model.Donation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DonationRepository extends JpaRepository<Donation, Long> {

    @Query("SELECT COALESCE(SUM(d.amount),0) FROM Donation d WHERE d.charity.id = :charityId")
    double findRaisedSum(long charityId);
}
