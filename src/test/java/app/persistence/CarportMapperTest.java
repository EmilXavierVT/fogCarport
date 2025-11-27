package app.persistence;

import app.entities.Carport;
import app.entities.StandardCarport;
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

public class CarportMapperTest {
    private final static Dotenv dotenv = Dotenv.load();
    private final static String USER = dotenv.get("DB-USER");
    private final static String PASSWORD = dotenv.get("DB-PASSWORD");
    private final static String URL = dotenv.get("DB-URL");
    private final static String DB = "carport";

    static ConnectionPool connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);

    @BeforeAll
    public static void setUpClass() {

        try (Connection connection = connectionPool.getConnection()) {
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("DROP TABLE IF EXISTS test_schema.carports");

                stmt.execute("DROP SEQUENCE IF EXISTS test_schema.carports_carport_id_seq CASCADE");

                //stmt.execute("CREATE TABLE test_schema.carports AS (SELECT * FROM public.carports) WITH NO DATA");
                stmt.execute("CREATE TABLE test_schema.carports (LIKE public.carports INCLUDING ALL)");

                stmt.execute("CREATE SEQUENCE test_schema.carports_carport_id_seq");
                stmt.execute("ALTER TABLE test_schema.carports ALTER COLUMN carport_id SET DEFAULT nextval('test_schema.carports_carport_id_seq')");

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() throws SQLException {
        try (Connection connection = connectionPool.getConnection()) {
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("DELETE FROM test_schema.carports");

                stmt.execute("SELECT setval('test_schema.carports_carport_id_seq', 1)");

                stmt.execute("INSERT INTO test_schema.carports VALUES " +
                        "(1,'Carport1',200,1,'This is a carport',1,'pdf')");

                stmt.execute("SELECT setval('test_schema.carports_carport_id_seq', COALESCE((SELECT MAX (carport_id)+1 FROM test_schema.carports), 1), false)");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Test
    public void createCarportTest() throws DatabaseException {
        Carport expectedcarport = new StandardCarport(2, "carport", 100, 1, "this is a carport", 1, "pdf");
        CarportMapper.SaveCarportInDB( "carport", 100, 1, "this is a carport", 1,"pdf",connectionPool);
        Carport real = CarportMapper.getCarportByID(2,connectionPool);

        assertEquals(expectedcarport.getCarportID(), real.getCarportID());
        assertEquals(expectedcarport.getName(),real.getName());
    }
}
