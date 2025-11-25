package app.persistence;

import app.entities.Carport;
import app.entities.StandardCarport;
import app.entities.User;
import app.exceptions.DatabaseException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

public class CarportMapperTest {
    private final static String USER = "postgres";
    private final static String PASSWORD = "postgres1234";
    private final static String URL = "jdbc:postgresql://128.199.42.25:5432/%s?currentSchema=test_schema";
    private final static String DB = "carport";

    static ConnectionPool connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);

    @BeforeAll
    public static void setUpClass() {

        try (Connection connection = connectionPool.getConnection()) {
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("DROP TABLE IF EXISTS test_schema.carports");

                stmt.execute("DROP SEQUENCE IF EXISTS test_schema.carports_carport_id_seq CASCADE");

                stmt.execute("CREATE TABLE test_schema.carports AS (SELECT * FROM public.carports) WITH NO DATA");

                stmt.execute("CREATE SEQUENCE test_schema.carports_carport_id_seq");
                stmt.execute("ALTER TABLE test_schema.carports ALTER COLUMN carport_id SET DEFAULT nextval('test_schema.carports_carport_id_seq')");

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void createCarportTest() throws DatabaseException {
        Carport expectedcarport = new StandardCarport(1, "carport", 100, 1, "this is a carport", 1, "pdf");
        CarportMapper.SaveCarportInDB( "carport", 100, 1, "this is a carport", 1,connectionPool);

        Carport real = CarportMapper.getCarportByID(1,connectionPool);
        assertEquals(expectedcarport, real);
    }
}
