package me.alexutzzu.teledon.persistence;

import me.alexutzzu.teledon.model.AuthUser;

import java.sql.SQLException;
import java.util.Optional;

public interface AuthUserRepository extends BasicRepository<AuthUser> {

    Optional<AuthUser> findByUsername(String username) throws SQLException;
}
