package app.services;

import app.entities.ProductInOrder;
import app.entities.Specification;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.SpecificationMapper;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

class CalculatorTest
{
    private final static Dotenv dotenv = Dotenv.load();
    private final static String USER = dotenv.get("DB-USER");
    private final static String PASSWORD = dotenv.get("DB-PASSWORD");
    private final static String URL = dotenv.get("DB-URL");
    private final static String DB = "carport";
    static ConnectionPool connectionPool = ConnectionPool.getInstance(USER,PASSWORD,URL,DB);

    @BeforeAll
    public static void setUpClass()
    {
        try (Connection connection = connectionPool.getConnection())
        {
            try (Statement stmt = connection.createStatement())
            {
                stmt.execute("DROP TABLE IF EXISTS test_schema.specifications");
                stmt.execute("DROP TABLE IF EXISTS test_schema.products");

                stmt.execute("DROP SEQUENCE IF EXISTS test_schema.specifications_specification_id_seq CASCADE");
                stmt.execute("DROP SEQUENCE IF EXISTS test_schema.products_product_id_seq CASCADE");

                stmt.execute("CREATE TABLE test_schema.specifications (LIKE public.specifications INCLUDING ALL)");
                stmt.execute("CREATE TABLE test_schema.products (LIKE public.products INCLUDING ALL)");

                stmt.execute("CREATE SEQUENCE test_schema.specifications_specification_id_seq");
                stmt.execute("CREATE SEQUENCE test_schema.products_product_id_seq");

                stmt.execute("ALTER TABLE test_schema.specifications ALTER COLUMN specification_id SET DEFAULT nextval('test_schema.specifications_specification_id_seq')");
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
                stmt.execute("DELETE FROM test_schema.specifications");
                stmt.execute("DELETE FROM test_schema.products");

                stmt.execute("SELECT setval('test_schema.specifications_specification_id_seq', 1)");
                stmt.execute("SELECT setval('test_schema.products_product_id_seq', 1)");

                stmt.execute("INSERT INTO test_schema.specifications VALUES " +
                        "(1,2000000710761,'Carport',2,true,22,3,26,28,1,780,600,380,380,780,600,530,540,505,210,530)");

                stmt.execute("INSERT INTO test_schema.products VALUES " +
                        "(22,'trykimprægneret stolpe','125x125 mm','stolper graves 90 cm i jord',110,22)");
                stmt.execute("INSERT INTO test_schema.products VALUES " +
                        "(3,'spærtræ','45x195 mm','Remme i sider, sadles ned i stolper',100,3)");
                stmt.execute("INSERT INTO test_schema.products VALUES " +
                        "(26,'spærtræ færdigsamlede 25 gr','145x145 mm','spær monteres på remme',70,3)");
                stmt.execute("INSERT INTO test_schema.products VALUES " +
                        "(28,'B&C dobbelt-S sort beton tagsten m/ 30 års garant','0','tag',50,23)");
                stmt.execute("INSERT INTO test_schema.products VALUES " +
                        "(1,'trykimp. Bræt','25x200 mm','understernbrædder til for- & bagende',15,1)");
                stmt.execute("INSERT INTO test_schema.products VALUES " +
                        "(9,'vægbeslag','50x50 mm','beslag til væg',20,9)");

                stmt.execute("SELECT setval('test_schema.specifications_specification_id_seq', COALESCE((SELECT MAX (specification_id)+1 FROM test_schema.specifications), 1), false)");
            }
            catch (SQLException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    @Test
    public void testCalculatePrice() throws DatabaseException, SQLException
    {
        Specification testSpecification = SpecificationMapper.getSpecificationByID(1,connectionPool);
        Calculator calc = new Calculator(testSpecification);
        calc.setConnectionPool(connectionPool);
        System.out.println(calc.getCostPrice());
    }
    @Test
    public void testItemList() throws SQLException, DatabaseException
    {
        Specification testSpec = SpecificationMapper.getSpecificationByID(1,connectionPool);
        Calculator calc = new Calculator(testSpec);

       for(ProductInOrder pio : calc.setItemList())
       {
           System.out.println(pio + "\n");
       }
    }
}