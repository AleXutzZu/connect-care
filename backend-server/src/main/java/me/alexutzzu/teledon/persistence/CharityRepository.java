package me.alexutzzu.teledon.persistence;

import me.alexutzzu.teledon.model.Charity;
import me.alexutzzu.teledon.model.dto.statistics.CharityDonationsStatisticsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CharityRepository extends JpaRepository<Charity, Long> {
    Page<Charity> findByNameContainingIgnoreCase(String name, Pageable pageable);

    List<Charity> findByNameContainingIgnoreCase(String name);

    @Query("SELECT " +
            "  FUNCTION('date_trunc', 'month', d.createdOn) AS month, " +
            "  COUNT(DISTINCT d.donor.id) AS donorCount, " +
            "  SUM(d.amount) AS totalAmount " +
            "FROM Donation d " +
            "WHERE d.charity.id = :charityId " +
            "AND d.createdOn >= :sinceDate " +
            "GROUP BY FUNCTION('date_trunc', 'month', d.createdOn) " +
            "ORDER BY FUNCTION('date_trunc', 'month', d.createdOn) ASC")
    List<CharityDonationsStatisticsDto> findMonthlyStatsByCharity(@Param("charityId") Long charityId,
                                                                  @Param("sinceDate") LocalDateTime sinceDate);
}
