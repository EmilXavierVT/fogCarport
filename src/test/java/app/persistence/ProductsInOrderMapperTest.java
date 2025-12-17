package app.persistence;

import app.entities.*;
import app.exceptions.DatabaseException;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
public class ProductsInOrderMapperTest
{
    private final static Dotenv dotenv = Dotenv.load();
    private final static String USER = dotenv.get("DB-USER");
    private final static String PASSWORD = dotenv.get("DB-PASSWORD");
    private final static String URL = dotenv.get("DB-URL");
    private final static String DB = "carport";
    static ConnectionPool connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);

    User tempUser = new User(1,"Emil","Thorsen",2200,"Farumgade",1,"2th","ex@tv.dk","1234",12345678,0);
    Order tempOrder = new Order (1,tempUser,LocalDate.parse("2025-11-25"));
    Product tempProduct = new Product(3,"spærtræ","45x195 mm","Remme i sider, sadles ned i stolper",100,3);

    ProductInOrder expectedProductInOrder = new ProductInOrder(1,tempOrder.getId(),tempProduct,2);

    @BeforeAll
    public static void setUpClass() throws SQLException
    {
        try (Connection connection = connectionPool.getConnection())
        {
            try (Statement stmt = connection.createStatement())
            {
                stmt.execute("DROP TABLE IF EXISTS test_schema.products_in_orders");
                stmt.execute("DROP TABLE IF EXISTS test_schema.orders");
                stmt.execute("DROP TABLE IF EXISTS test_schema.users");
                stmt.execute("DROP TABLE IF EXISTS test_schema.products");

                stmt.execute("DROP SEQUENCE IF EXISTS test_schema.products_in_orders_products_in_order_id_seq CASCADE");
                stmt.execute("DROP SEQUENCE IF EXISTS test_schema.orders_order_id_seq CASCADE");
                stmt.execute("DROP SEQUENCE IF EXISTS test_schema.users_user_id_seq CASCADE;");
                stmt.execute("DROP SEQUENCE IF EXISTS test_schema.products_product_id_seq CASCADE");


                stmt.execute("CREATE TABLE test_schema.products_in_orders (LIKE public.products_in_orders INCLUDING ALL)");
                stmt.execute("CREATE TABLE test_schema.orders (LIKE public.orders INCLUDING ALL)");
                stmt.execute("CREATE TABLE test_schema.users AS (SELECT * from public.users) WITH NO DATA");
                stmt.execute("CREATE TABLE test_schema.products (LIKE public.products INCLUDING ALL)");


                stmt.execute("CREATE SEQUENCE test_schema.products_in_orders_products_in_order_id_seq");
                stmt.execute("CREATE SEQUENCE test_schema.orders_order_id_seq");
                stmt.execute("CREATE SEQUENCE test_schema.users_user_id_seq");
                stmt.execute("CREATE SEQUENCE test_schema.products_product_id_seq");


                stmt.execute("ALTER TABLE test_schema.products_in_orders ALTER COLUMN products_in_order_id SET DEFAULT nextval('test_schema.products_in_orders_products_in_order_id_seq')");
                stmt.execute("ALTER TABLE test_schema.orders ALTER COLUMN order_id SET DEFAULT nextval('test_schema.orders_order_id_seq')");
                stmt.execute("ALTER TABLE test_schema.users ALTER COLUMN user_id SET DEFAULT nextval('test_schema.users_user_id_seq')");
                stmt.execute("ALTER TABLE test_schema.products ALTER COLUMN product_id SET DEFAULT nextval('test_schema.products_product_id_seq')");
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() throws SQLException
    {
        try (Connection connection = connectionPool.getConnection())
        {
            try (Statement stmt = connection.createStatement())
            {
                stmt.execute("DELETE FROM test_schema.products_in_orders");
                stmt.execute("DELETE FROM test_schema.orders");
                stmt.execute("DELETE FROM test_schema.users");
                stmt.execute("DELETE FROM test_schema.products");

                stmt.execute("SELECT setval('test_schema.products_in_orders_products_in_order_id_seq',1)");
                stmt.execute("SELECT setval('test_schema.orders_order_id_seq',1)");
                stmt.execute("SELECT setval('test_schema.users_user_id_seq', 1)");
                stmt.execute("SELECT setval('test_schema.products_product_id_seq',1)");

                stmt.execute("INSERT INTO test_schema.products_in_orders VALUES " +
                        "(1,1,3,2)");
                stmt.execute("INSERT INTO test_schema.products_in_orders VALUES " +
                        "(2,1,3,2)");
                stmt.execute("INSERT INTO test_schema.products_in_orders VALUES " +
                        "(3,2,3,2)");

                stmt.execute("INSERT INTO test_schema.orders VALUES " +
                        "(1,1,'2025-11-25')");

                stmt.execute("INSERT INTO test_schema.users VALUES " +
                        "(1,'Emil','Thorsen',2200,'Farumgade',1,'2th','ex@tv.dk','1234',0)");

                stmt.execute("INSERT INTO test_schema.products VALUES " +
                        "(3,'spærtræ','45x195 mm','Remme i sider, sadles ned i stolper',100,3)");

                stmt.execute("SELECT setval('test_schema.products_in_orders_products_in_order_id_seq', COALESCE((SELECT MAX (products_in_order_id)+1 FROM test_schema.products_in_orders), 1), false)");
                stmt.execute("SELECT setval('test_schema.orders_order_id_seq', COALESCE((SELECT MAX (order_id)+1 FROM test_schema.orders), 1), false)");
                stmt.execute("SELECT setval('test_schema.users_user_id_seq', COALESCE((SELECT MAX(user_id)+1 FROM test_schema.users), 1), false)");
                stmt.execute("SELECT setval('test_schema.products_product_id_seq', COALESCE((SELECT MAX (product_id)+1 FROM test_schema.products), 1), false)");
            }
            catch (SQLException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    @Test
    public void findProductInOrder() throws DatabaseException
    {
        assertEquals(expectedProductInOrder,ProductInOrderMapper.getProductInOrderById(1,connectionPool));
    }

    @Test
    public void createProductInOrder() throws DatabaseException, SQLException
    {
        assertEquals(3,TestMapper.count("products_in_orders",connectionPool));
        ProductInOrderMapper.createProductInOrder(tempOrder.getId(),tempProduct,2,connectionPool);
        assertEquals(4,TestMapper.count("products_in_orders",connectionPool));
    }

    @Test
    public void updateProductInOrder() throws DatabaseException
    {
    assertEquals(expectedProductInOrder,ProductInOrderMapper.getProductInOrderById(1,connectionPool));
    ProductInOrder updatedProductInOrder = new ProductInOrder(1,tempOrder.getId(),tempProduct,1);
    ProductInOrderMapper.updateProductInOrder(1,1,connectionPool);
    assertEquals(updatedProductInOrder,ProductInOrderMapper.getProductInOrderById(1,connectionPool));
    }

    @Test
    public void deleteProductInOrder() throws DatabaseException, SQLException
    {
        assertEquals(3,TestMapper.count("products_in_orders",connectionPool));
        ProductInOrderMapper.deleteProductInOrder(1,connectionPool);
        assertEquals(2,TestMapper.count("products_in_orders",connectionPool));
    }

    @Test
    public void GetAllProducts() throws DatabaseException {
        List<ProductInOrder> allProductInOrdersByOrderID = ProductInOrderMapper.getAllProductsInOrderByOrderID(1,connectionPool);
        assertEquals(2,allProductInOrdersByOrderID.size());
    }
}
