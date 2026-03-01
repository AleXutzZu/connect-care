package me.alexutzzu.teledon.persistence;

import java.sql.SQLException;
import java.util.Optional;

/**
 * Basic repository for CRUD operations
 *
 * @param <T> the type managed by this repository
 */
public interface BasicRepository<T> {

    /**
     * Inserts a new row into the database
     *
     * @param data the data for the object. The id field is ignored
     * @return the row that was inserted in the database, but with the id field populated by the database
     * @throws SQLException if the query fails
     */
    T create(T data) throws SQLException;

    /**
     * Find the record with the given id
     *
     * @return an optional containing the record
     * @throws SQLException if the query fails
     */
    Optional<T> findById(Long id) throws SQLException;

    /**
     * Updates the record in the database with the new data. The id field must be present so the target record
     * can be identified
     *
     * @param data the data for the object. The id field is mandatory. Other fields may be null and will be
     *             interpreted as "do not modify the original value in the database"
     * @return the record with the updated data
     * @throws SQLException if the query fails
     */
    T update(T data) throws SQLException;

    /**
     * Deletes the record by the given id. If the record does not exist, this method has no impact on the database
     *
     * @param id the id to lookup
     * @throws SQLException if the query fails
     */
    void deleteById(Long id) throws SQLException;
}
