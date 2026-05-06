package me.alexutzzu.teledon.persistence;

import me.alexutzzu.teledon.model.Donor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DonorRepository extends JpaRepository<Donor, Long> {

}
