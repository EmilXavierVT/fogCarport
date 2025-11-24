package app.persistence;




import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.Database;
import app.persistence.UserMapper;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

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
        } try(Statement statement = connectionPool.getConnection().createStatement()){

        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }



        @Test
    void getUserByID() throws DatabaseException, DatabaseException {
        User real = UserMapper.getUserByID(1);
        User expected = new User(1,"Frederik","Edvardsen",2450,"vej",39,"1tv","fred@dk.dk", "1234",1);

        assertEquals(real.getFirstName(),expected.getFirstName());
        assertEquals(real.getLastName(),expected.getLastName());
        assertEquals(real.getZipCode(),expected.getZipCode());
        assertEquals(real.getStreetName(),expected.getStreetName());
        assertEquals(real.getHouseNumber(),expected.getHouseNumber());
        assertEquals(real.getFloor(),expected.getFloor());
        assertEquals(real.getEmail(),expected.getEmail());
        assertEquals(real.getPassword(),expected.getPassword());
        assertEquals(real.getRole(),expected.getRole());
    }
}