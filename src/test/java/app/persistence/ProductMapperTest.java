package app.persistence;

import app.entities.*;
import app.exceptions.DatabaseException;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ProductMapperTest {

    private final static Dotenv dotenv = Dotenv.load();
    private final static String USER = dotenv.get("DB-USER");
    private final static String PASSWORD = dotenv.get("DB-PASSWORD");
    private final static String URL = dotenv.get("DB-URL");
    private final static String DB = "carport";

    static ConnectionPool connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);

    @BeforeAll
    public static void setUpClass() throws SQLException {

        try (Connection connection = connectionPool.getConnection()) {
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("DROP TABLE IF EXISTS test_schema.products");

                stmt.execute("DROP SEQUENCE IF EXISTS test_schema.products_product_id_seq CASCADE");

                stmt.execute("CREATE TABLE test_schema.products (LIKE public.products INCLUDING ALL)");

                stmt.execute("CREATE SEQUENCE test_schema.products_product_id_seq");
                stmt.execute("ALTER TABLE test_schema.products ALTER COLUMN product_id SET DEFAULT nextval('test_schema.products_product_id_seq')");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @BeforeEach
    void setUp() throws SQLException {
        try (Connection connection = connectionPool.getConnection()) {
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("DELETE FROM test_schema.products");

                stmt.execute("SELECT setval('test_schema.products_product_id_seq',1)");

                stmt.execute("INSERT INTO test_schema.products VALUES " +
                        "(1,'screw','2x2','it s a screw', 100,1)");

                stmt.execute("SELECT setval('test_schema.products_product_id_seq', COALESCE((SELECT MAX (product_id)+1 FROM test_schema.products), 1), false)");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
    @Test
    void createOrderTest() throws SQLException, DatabaseException {
    Product expected = new Product(2,"wood","10*200","its a pice of wood",200,2);
    ProductMapper.saveProduct("wood","10*200","its a pice of wood",200,2,connectionPool);
    Product real = ProductMapper.getProductByID(2,connectionPool);

    assertEquals(expected,real);
    }
}

