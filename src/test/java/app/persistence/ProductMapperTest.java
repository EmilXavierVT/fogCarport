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
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class ProductMapperTest
{
    private final static Dotenv dotenv = Dotenv.load();
    private final static String USER = dotenv.get("DB-USER");
    private final static String PASSWORD = dotenv.get("DB-PASSWORD");
    private final static String URL = dotenv.get("DB-URL");
    private final static String DB = "carport";
    static ConnectionPool connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);

    Product expectedProduct = new Product(3,"spærtræ","45x195 mm","Remme i sider, sadles ned i stolper",100,3);

    @BeforeAll
    public static void setUpClass() throws SQLException
    {
        try (Connection connection = connectionPool.getConnection())
        {
            try (Statement stmt = connection.createStatement())
            {
                stmt.execute("DROP TABLE IF EXISTS test_schema.products");

                stmt.execute("DROP SEQUENCE IF EXISTS test_schema.products_product_id_seq CASCADE");

                stmt.execute("CREATE TABLE test_schema.products (LIKE public.products INCLUDING ALL)");

                stmt.execute("CREATE SEQUENCE test_schema.products_product_id_seq");
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
                stmt.execute("DELETE FROM test_schema.products");

                stmt.execute("SELECT setval('test_schema.products_product_id_seq',1)");

                stmt.execute("INSERT INTO test_schema.products VALUES " +
                        "(3,'spærtræ','45x195 mm','Remme i sider, sadles ned i stolper',100,3)");
                stmt.execute("INSERT INTO test_schema.products VALUES " +
                        "(26,'spærtræ færdigsamlede 25 gr','145x145 mm','spær monteres på remme',70,3)");
                stmt.execute("INSERT INTO test_schema.products VALUES " +
                        "(28,'B&C dobbelt-S sort beton tagsten m/ 30 års garant','0','tag',50,23)");
                stmt.execute("INSERT INTO test_schema.products VALUES " +
                        "(1,'trykimp. Bræt','25x200 mm','understernbrædder til for- & bagende',15,1)");

                stmt.execute("SELECT setval('test_schema.products_product_id_seq', COALESCE((SELECT MAX (product_id)+1 FROM test_schema.products), 1), false)");
            }
            catch (SQLException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    @Test
    public void findOrder() throws SQLException, DatabaseException
    {
    assertEquals(expectedProduct,ProductMapper.getProductByID(3,connectionPool));
    }

    @Test
    public void createOrderTest() throws SQLException, DatabaseException
    {
        assertEquals(4,TestMapper.count("products",connectionPool));
        ProductMapper.saveProduct("wood","10*200","its a pice of wood",200,2,connectionPool);
        assertEquals(5,TestMapper.count("products",connectionPool));
    }

    @Test
    public void updateOrderTest() throws SQLException, DatabaseException
    {
        assertEquals(expectedProduct,ProductMapper.getProductByID(3,connectionPool));
        ProductMapper.updateOrder(3,"spærtræ","50x195 mm","Remme i sider, sadles ned i stolper",100,3,connectionPool);
        Product productAfterUpdate = new Product(3,"spærtræ","50x195 mm","Remme i sider, sadles ned i stolper",100,3);
        assertEquals(productAfterUpdate,ProductMapper.getProductByID(3,connectionPool));
    }

    @Test
    public void deleteProductTest() throws SQLException, DatabaseException
    {
        assertEquals(4,TestMapper.count("products",connectionPool));
        ProductMapper.deleteProduct(3,connectionPool);
        assertEquals(3,TestMapper.count("products",connectionPool));
    }
    @Test
    public void getallProductsTest() throws SQLException, DatabaseException
    {
        List<Product> allProducts = ProductMapper.getAllProducts(connectionPool);
        assertEquals(4,allProducts.size());
    }
}

