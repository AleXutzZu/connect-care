package me.alexutzzu.teledon.persistence;

import me.alexutzzu.teledon.model.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthUserRepository extends JpaRepository<AuthUser, Long> {
    Optional<AuthUser> findAuthUserByUsername(String username);
}
