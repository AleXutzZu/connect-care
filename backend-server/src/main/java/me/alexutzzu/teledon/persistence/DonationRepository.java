package me.alexutzzu.teledon.persistence;

import me.alexutzzu.teledon.model.Donation;
import me.alexutzzu.teledon.model.dto.statistics.DonationStatisticsDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

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
}
