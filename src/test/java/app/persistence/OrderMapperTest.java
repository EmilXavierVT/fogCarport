package app.persistence;

import app.entities.Carport;
import app.entities.Order;
import app.entities.StandardCarport;
import app.entities.User;
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

public class OrderMapperTest {
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
                stmt.execute("DROP TABLE IF EXISTS test_schema.orders");

                stmt.execute("DROP SEQUENCE IF EXISTS test_schema.orders_order_id_seq CASCADE");

                stmt.execute("CREATE TABLE test_schema.orders (LIKE public.orders INCLUDING ALL)");

                stmt.execute("CREATE SEQUENCE test_schema.orders_order_id_seq");
                stmt.execute("ALTER TABLE test_schema.orders ALTER COLUMN order_id SET DEFAULT nextval('test_schema.orders_order_id_seq')");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() throws SQLException {
        try (Connection connection = connectionPool.getConnection()) {
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("DELETE FROM test_schema.orders");

                stmt.execute("SELECT setval('test_schema.orders_order_id_seq',1)");

                stmt.execute("INSERT INTO test_schema.orders VALUES " +
                        "(1,1,'2025-11-25')");
                stmt.execute("SELECT setval('test_schema.orders_order_id_seq', COALESCE((SELECT MAX (order_id)+1 FROM test_schema.orders), 1), false)");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
    @Test
    public void createOrderTest() throws SQLException, DatabaseException {
        User temp = new User(1,"Emil","Thorsen",2200,"Farumgade",1,"2th","ex@tv.dk","1234",0);
        Order expectedOrder = new Order(2,temp, LocalDate.parse("2025-11-27"));
        OrderMapper.saveOrder(temp.getUserId(),LocalDate.parse("2025-11-27"),connectionPool);
        Order real = OrderMapper.getOrderByID(2,connectionPool);

        assertEquals(expectedOrder.getId(),real.getId());
        assertEquals(expectedOrder.getDate(),real.getDate());
        assertEquals(expectedOrder.getUserId(),real.getUserId());
    }
}
