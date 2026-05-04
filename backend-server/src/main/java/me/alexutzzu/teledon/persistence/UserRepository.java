package me.alexutzzu.teledon.persistence;

import me.alexutzzu.teledon.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findAuthUserByUsername(String username);
}
