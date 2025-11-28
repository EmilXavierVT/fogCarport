package app.persistence;

import app.entities.User;
import app.exceptions.DatabaseException;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private final static Dotenv dotenv = Dotenv.load();
    private final static String USER = dotenv.get("DB-USER");
    private final static String PASSWORD = dotenv.get("DB-PASSWORD");
    private final static String URL = dotenv.get("DB-URL");
    private final static String DB = "carport";

    static ConnectionPool connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);

    @BeforeAll
    public static void setUpClass()
    {

        try (Connection testConnection = connectionPool.getConnection())
        {
            try (Statement stmt = testConnection.createStatement())
            {
                // The test schema is already created, so we only need to delete/create test tables
                stmt.execute("DROP TABLE IF EXISTS test_schema.users");

                stmt.execute("DROP SEQUENCE IF EXISTS test_schema.users_user_id_seq CASCADE;");

                // Create tables as copy of original public schema structure
                stmt.execute("CREATE TABLE test_schema.users AS (SELECT * from public.users) WITH NO DATA");


                // Create sequences for auto generating id's for members and sports
                stmt.execute("CREATE SEQUENCE test_schema.users_user_id_seq");
                stmt.execute("ALTER TABLE test_schema.users ALTER COLUMN user_id SET DEFAULT nextval('test_schema.users_user_id_seq')");

            }
        } catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() throws SQLException {
        try (Connection testConnection = connectionPool.getConnection()) {
            try (Statement stmt = testConnection.createStatement()) {
                // Remove all rows from all tables
                stmt.execute("DELETE FROM test_schema.users");


                // Reset the sequence number
                stmt.execute("SELECT setval('test_schema.users_user_id_seq', 1)");

                // Insert rows
                stmt.execute("INSERT INTO test_schema.users VALUES " +
                        "(1,'Emil','Thorsen',2200,'Farumgade',1,'2th','ex@tv.dk','1234',1)");


                // Set sequence to continue from the largest member_id
                stmt.execute("SELECT setval('test_schema.users_user_id_seq', COALESCE((SELECT MAX(user_id)+1 FROM test_schema.users), 1), false)");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
    @Test
    public void findUserByIDTest() throws DatabaseException {
        User expected = new User(1,"Emil","Thorsen",2200,"Farumgade",1,"2th","ex@tv.dk","1234",1);
        User Real = UserMapper.getUserByID(1,connectionPool);
        assertEquals(expected,Real);
    }

    @Test
    public void createUserTest() throws DatabaseException
    {
        User user = UserMapper.createUser("Frederik", "Edvardsen", 2450, "Offensbachvej", 39, "1tv", "Fred@dk.dk", "1234", connectionPool);
        assertNotNull(user);
        assertEquals("Frederik", user.getFirstName());
        assertEquals(2,user.getUserId());
    }
}