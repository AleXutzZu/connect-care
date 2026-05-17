package me.alexutzzu.teledon.persistence;

import me.alexutzzu.teledon.model.Donor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DonorRepository extends JpaRepository<Donor, Long> {
    long countByCreatedOnBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT d FROM Donor d WHERE " +
            "LOWER(CONCAT(d.firstName, ' ', d.lastName)) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(d.address) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(d.phoneNumber) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Donor> search(String search, Pageable pageable);

    @Query("SELECT d FROM Donor d WHERE " +
            "LOWER(CONCAT(d.firstName, ' ', d.lastName)) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(d.address) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(d.phoneNumber) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Donor> search(String search);
}
