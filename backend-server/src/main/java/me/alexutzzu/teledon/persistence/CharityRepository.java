package me.alexutzzu.teledon.persistence;

import me.alexutzzu.teledon.model.Charity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CharityRepository extends JpaRepository<Charity, Long> {
}
