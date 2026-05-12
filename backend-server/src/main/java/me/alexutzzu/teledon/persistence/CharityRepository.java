package me.alexutzzu.teledon.persistence;

import me.alexutzzu.teledon.model.Charity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CharityRepository extends JpaRepository<Charity, Long> {
    Page<Charity> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
