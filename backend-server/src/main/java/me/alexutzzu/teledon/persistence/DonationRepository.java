package me.alexutzzu.teledon.persistence;

import me.alexutzzu.teledon.model.Donation;
import me.alexutzzu.teledon.model.dto.statistics.CharityStatisticsDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {

    @Query("SELECT " +
            "  FUNCTION('date_trunc', 'month', d.createdOn) AS month, " +
            "  COUNT(DISTINCT d.donor.id) AS donorCount, " +
            "  SUM(d.amount) AS totalAmount " +
            "FROM Donation d " +
            "WHERE d.charity.id = :charityId " +
            "AND d.createdOn >= :sinceDate " +
            "GROUP BY FUNCTION('date_trunc', 'month', d.createdOn) " +
            "ORDER BY FUNCTION('date_trunc', 'month', d.createdOn) ASC")
    List<CharityStatisticsDto> findMonthlyStats(@Param("charityId") Long charityId,
                                                @Param("sinceDate") LocalDateTime sinceDate);
}
