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
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OrderMapperTest {
    private final static Dotenv dotenv = Dotenv.load();
    private final static String USER = dotenv.get("DB-USER");
    private final static String PASSWORD = dotenv.get("DB-PASSWORD");
    private final static String URL = dotenv.get("DB-URL");
    private final static String DB = "carport";

    static ConnectionPool connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);

    User tempUser = new User(1,"Emil","Thorsen",2200,"Farumgade",1,"2th","ex@tv.dk","1234",0);
    Order expectedOrder = new Order (1,tempUser,LocalDate.parse("2025-11-25"));

    @BeforeAll
    public static void setUpClass() throws SQLException {

        try (Connection connection = connectionPool.getConnection()) {
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("DROP TABLE IF EXISTS test_schema.orders");
                stmt.execute("DROP TABLE IF EXISTS test_schema.users");

                stmt.execute("DROP SEQUENCE IF EXISTS test_schema.orders_order_id_seq CASCADE");
                stmt.execute("DROP SEQUENCE IF EXISTS test_schema.users_user_id_seq CASCADE;");

                stmt.execute("CREATE TABLE test_schema.orders (LIKE public.orders INCLUDING ALL)");
                stmt.execute("CREATE TABLE test_schema.users AS (SELECT * from public.users) WITH NO DATA");

                stmt.execute("CREATE SEQUENCE test_schema.orders_order_id_seq");
                stmt.execute("CREATE SEQUENCE test_schema.users_user_id_seq");

                stmt.execute("ALTER TABLE test_schema.orders ALTER COLUMN order_id SET DEFAULT nextval('test_schema.orders_order_id_seq')");
                stmt.execute("ALTER TABLE test_schema.users ALTER COLUMN user_id SET DEFAULT nextval('test_schema.users_user_id_seq')");

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
                stmt.execute("DELETE FROM test_schema.users");


                stmt.execute("SELECT setval('test_schema.orders_order_id_seq',1)");
                stmt.execute("SELECT setval('test_schema.users_user_id_seq', 1)");

                stmt.execute("INSERT INTO test_schema.orders VALUES " +
                        "(1,1,'2025-11-25')");
                stmt.execute("INSERT INTO test_schema.orders VALUES " +
                        "(2,1,'2025-11-28')");

                stmt.execute("INSERT INTO test_schema.users VALUES " +
                        "(1,'Emil','Thorsen',2200,'Farumgade',1,'2th','ex@tv.dk','1234',0)");

                stmt.execute("SELECT setval('test_schema.orders_order_id_seq', COALESCE((SELECT MAX (order_id)+1 FROM test_schema.orders), 1), false)");
                stmt.execute("SELECT setval('test_schema.users_user_id_seq', COALESCE((SELECT MAX(user_id)+1 FROM test_schema.users), 1), false)");

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
    @Test
    public void findOrderTest(){
        assertEquals(expectedOrder,OrderMapper.getOrderByID(1,connectionPool));
    }

    @Test
    public void createOrderTest() throws SQLException, DatabaseException {
        assertEquals(2,TestMapper.count("orders",connectionPool));
        OrderMapper.saveOrder(tempUser.getUserId(),LocalDate.parse("2025-11-27"),connectionPool);
        assertEquals(3,TestMapper.count("orders",connectionPool));
    }
    @Test
    public void updateOrderTest() throws SQLException, DatabaseException {
        assertEquals(expectedOrder,OrderMapper.getOrderByID(1,connectionPool));
        OrderMapper.updateOrder(1,LocalDate.parse("2025-11-26"),connectionPool);
        Order orderAfterUpdate = new Order(1,tempUser,LocalDate.parse("2025-11-26"));
        assertEquals(orderAfterUpdate,OrderMapper.getOrderByID(1,connectionPool));
    }

    @Test
    public void deleteOrderTest() throws SQLException, DatabaseException {
        assertEquals(2,TestMapper.count("orders",connectionPool));
        OrderMapper.deleteOrder(1,connectionPool);
        assertEquals(1,TestMapper.count("orders",connectionPool));
    }

    @Test
    public void getAllOrders() throws DatabaseException {
        List<Order> orders = OrderMapper.getAllOrders(connectionPool);
        assertEquals(2,orders.size());
    }
}

