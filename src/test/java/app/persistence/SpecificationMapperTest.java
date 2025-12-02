package app.persistence;

import app.entities.Carport;
import app.entities.Specification;
import app.entities.StandardCarport;
import app.exceptions.DatabaseException;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

public class SpecificationMapperTest {
    private final static Dotenv dotenv = Dotenv.load();
    private final static String USER = dotenv.get("DB-USER");
    private final static String PASSWORD = dotenv.get("DB-PASSWORD");
    private final static String URL = dotenv.get("DB-URL");
    private final static String DB = "carport";

    static ConnectionPool connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);

    public SpecificationMapperTest() throws SQLException, DatabaseException {
    }


    @BeforeAll
    public static void setUpClass() {

        try (Connection connection = connectionPool.getConnection()) {
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("DROP TABLE IF EXISTS test_schema.specifications");

                stmt.execute("DROP SEQUENCE IF EXISTS test_schema.specifications_specification_id_seq CASCADE");

                //stmt.execute("CREATE TABLE test_schema.carports AS (SELECT * FROM public.carports) WITH NO DATA");
                stmt.execute("CREATE TABLE test_schema.specifications (LIKE public.specifications INCLUDING ALL)");

                stmt.execute("CREATE SEQUENCE test_schema.specifications_specification_id_seq");
                stmt.execute("ALTER TABLE test_schema.specifications ALTER COLUMN specification_id SET DEFAULT nextval('test_schema.specifications_specification_id_seq')");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
        @BeforeEach
        void setUp() throws SQLException {
            try (Connection connection = connectionPool.getConnection()) {
                try (Statement stmt = connection.createStatement()) {
                    stmt.execute("DELETE FROM test_schema.specifications");

                    stmt.execute("SELECT setval('test_schema.specifications_specification_id_seq', 1)");

                    stmt.execute("INSERT INTO test_schema.specifications VALUES " +
                            "(1,1234,'Carport','2 cars',true,8,8,8,8,8,100,200,50,50,100,200,10,200,200,10,10)");

                    stmt.execute("SELECT setval('test_schema.specifications_specification_id_seq', COALESCE((SELECT MAX (specification_id)+1 FROM test_schema.specifications), 1), false)");
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        @Test
    public void CreateSpecificationTest() throws SQLException, DatabaseException {
        Specification expected = new Specification(1,1234,"Carport","1 Car",true,
                10,10,10,10,10,100,200,50,
                50,100,200,15,100,200,
                15,200);
        SpecificationMapper.CreateSpecification(1234,"Carport","1 Car",true,
                10,10,10,10,10,100,200,50,
                50,100,200,15,100,200,
                15,200,connectionPool);
        Specification real = SpecificationMapper.getSpecificationByID(2,connectionPool);
        assertEquals(expected.getSpecificationId(),real.getSpecificationId());
        }



    }

