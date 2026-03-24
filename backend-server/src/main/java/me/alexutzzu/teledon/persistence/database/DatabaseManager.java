package me.alexutzzu.teledon.persistence.database;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseManager {
    private static DatabaseManager instance;

    private final Properties properties = new Properties();

    private DatabaseManager() throws IOException {
        InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties");

        if (input == null) {
            throw new IllegalStateException("application.properties is missing");
        }

        properties.load(input);
    }


    public static DatabaseManager getInstance() throws IOException {
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

    @SuppressWarnings("unchecked")
    public static <T> T getRepositoryInstance(Class<T> repoClass, Class<? extends T> implementationClass) throws IOException {
        try {
            T repository = implementationClass.getConstructor(DatabaseManager.class).newInstance(getInstance());

            return (T) Proxy.newProxyInstance(
                    repoClass.getClassLoader(),
                    new Class<?>[]{repoClass},
                    new LoggingHandler(repository));

        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
