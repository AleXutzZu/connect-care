package me.alexutzzu.teledon.persistence;

import me.alexutzzu.teledon.model.Donor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DonorRepository extends JpaRepository<Donor, Long> {

}
