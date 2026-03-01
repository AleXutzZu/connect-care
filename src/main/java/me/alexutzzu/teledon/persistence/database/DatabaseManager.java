package me.alexutzzu.teledon.persistence.database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseManager {
    private static DatabaseManager instance;

    private final Properties properties = new Properties();

    private DatabaseManager() throws IOException, SQLException {
        InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties");

        if (input == null) {
            throw new IllegalStateException("application.properties is missing");
        }

        properties.load(input);
    }


    public static DatabaseManager getInstance() throws IOException, SQLException {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                properties.getProperty("db.url"),
                properties.getProperty("db.username"),
                properties.getProperty("db.password"));
    }
}
