package app.persistence;

import app.entities.User;
import app.exceptions.DatabaseException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private final static String USER = "postgres";
    private final static String PASSWORD = "postgres1234";
    private final static String URL = "jdbc:postgresql://128.199.42.25:5432/%s?currentSchema=test_schema";
    private final static String DB = "carport";

    static ConnectionPool connectionPool = ConnectionPool.getInstance(USER,PASSWORD,URL,DB);

    @BeforeAll
    public static void setUpClass() throws SQLException {
        try {
            Connection Connection = connectionPool.getConnection();
            UserMapper userMapper = new UserMapper();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try (Statement statement = connectionPool.getConnection().createStatement()) {

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }}