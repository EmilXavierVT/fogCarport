package app.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private Connection connection;
    private final String USER;
    private final String PASSWORD;
    private final String URL;

    public Database(String user, String password, String url) throws ClassNotFoundException {
        USER = user;
        PASSWORD = password;
        URL = url;
        Class.forName("org.postgresql.Driver");
    }

    public Connection connect() throws SQLException {
        connection = DriverManager.getConnection(URL, USER, PASSWORD);
        return connection;
    }
}