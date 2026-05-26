package me.alexutzzu.teledon.persistence;

import me.alexutzzu.teledon.model.Donation;
import me.alexutzzu.teledon.model.dto.statistics.DonationStatisticsDto;
import me.alexutzzu.teledon.model.dto.statistics.HighestDonationInfoDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {

    @Query("SELECT FUNCTION('date_trunc', 'day', d.createdOn) AS date ,SUM(d.amount) AS totalAmount," +
            "COUNT(d.id) AS donationCount " +
            "FROM Donation d WHERE d.createdOn >= :sinceDate " +
            "GROUP BY FUNCTION('date_trunc', 'day', d.createdOn) " +
            "ORDER BY FUNCTION('date_trunc', 'day', d.createdOn) ASC")
    List<DonationStatisticsDto> findDailyStats(@Param("sinceDate") LocalDateTime since);


    @Query("SELECT COUNT(DISTINCT d.donor.id) FROM Donation d WHERE d.createdOn BETWEEN :start AND :end")
    long countDistinctDonorByCreatedOnBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    int countByDonorId(Long donorId);

    @Query("SELECT AVG(d.amount) FROM Donation d WHERE d.donor.id = :donorId")
    Optional<Double> getAverageDonationByDonor(@Param("donorId") Long donorId);

    @Query("SELECT d.charity.name AS charityName, d.amount AS amount FROM Donation d WHERE d.donor.id = :donorId ORDER BY d.amount DESC LIMIT 1")
    Optional<HighestDonationInfoDto> findHighestDonationByDonor(@Param("donorId") Long donorId);

    @Query("SELECT d.createdOn FROM Donation d WHERE d.donor.id = :donorId ORDER BY d.createdOn DESC LIMIT 1")
    Optional<LocalDateTime> findLastDonationByDonor(@Param("donorId") Long donorId);
}
