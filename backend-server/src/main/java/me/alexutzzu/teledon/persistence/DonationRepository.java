package me.alexutzzu.teledon.persistence;

import me.alexutzzu.teledon.model.Donation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DonationRepository extends JpaRepository<Donation, Long> {
}
