package app.persistence;

import app.entities.*;
import app.exceptions.DatabaseException;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.PreparedStatement;
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

    Product post = new Product(22, "trykimprægneret stolpe", "125x125 mm", "stolper graves 90 cm i jord", 110, 22);
    Product beam = new Product(3, "spærtræ", "45x195 mm", "Remme i sider, sadles ned i stolper", 100, 3);
    Product rafter = new Product(26, "spærtræ færdigsamlede 25 gr", "145x145 mm", "spær monteres på remme", 70, 3);
    Product roof = new Product(28, "B&C dobbelt-S sort beton tagsten m/ 30 års garant", "0", "tag", 50, 23);
    Product fasciaBoard = new Product(1, "trykimp. Bræt", "25x200 mm", "understernbrædder til for- & bagende", 15, 1);

    Specification temp = new Specification(1, 2000000710761L, "Carport", 2, true,
            post, beam, rafter, roof, fasciaBoard, 780, 600, 380,
            380, 780, 600, 530, 540, 505,
            210, 530);

    StandardCarport expectedcarport = new StandardCarport(1, "CARPORT CP02HUR DOBBELT 6,0X7,8M MED REDSKABSRUM 2,1X5,1M HØJ REJSNING", 54998, 2, "Bred byg selv-carport 6,00 x 7,80 m. med høj rejsning og plads til 2 biler Stort redskabsrum bagerst i carport Carporten er åben på begge sider, men kan lukkes efter ønske. Leveres med trykimprægnerede stolper, stern og vindskeder, færdigsamlede spær direkte fra fabrik, lodret beklædning på gavle og skur, samt BogC sort dobbelt-S til tagbeklædning. Alle skruer, bolte og beslag, samt udførlig byggevejledning medfølger. Carporten kan også oplevelses i Fog Helsinge. NB! Leveres som Byg-selv sæt - usamlet og ubehandlet. Passer målene på denne carport ikke med dine ønsker? Vi kan skræddersy en carport på specialmål til dig. Klik på linket Bestil tilbud på carport i specialmål, som du finder under Links og dokumenter nedenfor.", temp, "2000000710761.pdf");
    UserDefinedCarport expectedUDC = new UserDefinedCarport(2,"carport",10000,3,"its a special carport",temp);

    @BeforeAll
    public static void setUpClass() {
        //test specification


        try (Connection connection = connectionPool.getConnection()) {
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("DROP TABLE IF EXISTS test_schema.carports");
                stmt.execute("DROP TABLE IF EXISTS test_schema.specifications");
                stmt.execute("DROP TABLE IF EXISTS test_schema.products");


                stmt.execute("DROP SEQUENCE IF EXISTS test_schema.carports_carport_id_seq CASCADE");
                stmt.execute("DROP SEQUENCE IF EXISTS test_schema.specifications_specification_id_seq CASCADE");
                stmt.execute("DROP SEQUENCE IF EXISTS test_schema.products_product_id_seq CASCADE");

                //stmt.execute("CREATE TABLE test_schema.carports AS (SELECT * FROM public.carports) WITH NO DATA");
                stmt.execute("CREATE TABLE test_schema.carports (LIKE public.carports INCLUDING ALL)");
                stmt.execute("CREATE TABLE test_schema.specifications (LIKE public.specifications INCLUDING ALL)");
                stmt.execute("CREATE TABLE test_schema.products (LIKE public.products INCLUDING ALL)");

                stmt.execute("CREATE SEQUENCE test_schema.carports_carport_id_seq");
                stmt.execute("CREATE SEQUENCE test_schema.specifications_specification_id_seq");
                stmt.execute("CREATE SEQUENCE test_schema.products_product_id_seq");


                stmt.execute("ALTER TABLE test_schema.carports ALTER COLUMN carport_id SET DEFAULT nextval('test_schema.carports_carport_id_seq')");
                stmt.execute("ALTER TABLE test_schema.specifications ALTER COLUMN specification_id SET DEFAULT nextval('test_schema.specifications_specification_id_seq')");
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
                stmt.execute("DELETE FROM test_schema.carports");
                stmt.execute("DELETE FROM test_schema.specifications");
                stmt.execute("DELETE FROM test_schema.products");


                stmt.execute("SELECT setval('test_schema.carports_carport_id_seq', 1)");
                stmt.execute("SELECT setval('test_schema.specifications_specification_id_seq', 1)");
                stmt.execute("SELECT setval('test_schema.products_product_id_seq',1)");

                //Carport 1
                stmt.execute("INSERT INTO test_schema.carports VALUES " +
                        "(1,'CARPORT CP02HUR DOBBELT 6,0X7,8M MED REDSKABSRUM 2,1X5,1M HØJ REJSNING',54998,2,'Bred byg selv-carport 6,00 x 7,80 m. med høj rejsning og plads til 2 biler Stort redskabsrum bagerst i carport Carporten er åben på begge sider, men kan lukkes efter ønske. Leveres med trykimprægnerede stolper, stern og vindskeder, færdigsamlede spær direkte fra fabrik, lodret beklædning på gavle og skur, samt BogC sort dobbelt-S til tagbeklædning. Alle skruer, bolte og beslag, samt udførlig byggevejledning medfølger. Carporten kan også oplevelses i Fog Helsinge. NB! Leveres som Byg-selv sæt - usamlet og ubehandlet. Passer målene på denne carport ikke med dine ønsker? Vi kan skræddersy en carport på specialmål til dig. Klik på linket Bestil tilbud på carport i specialmål, som du finder under Links og dokumenter nedenfor.',1,'2000000710761.pdf')");

                //specification 1
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


                stmt.execute("SELECT setval('test_schema.carports_carport_id_seq', COALESCE((SELECT MAX (carport_id)+1 FROM test_schema.carports), 1), false)");
                stmt.execute("SELECT setval('test_schema.specifications_specification_id_seq', COALESCE((SELECT MAX (specification_id)+1 FROM test_schema.specifications), 1), false)");

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Test
    public void findCarportByIdTest() throws DatabaseException {
        Carport real = CarportMapper.getCarportByID(1, connectionPool);
        assertEquals(expectedcarport, real);
    }

    @Test
    public void createStandardCarportTest() throws DatabaseException, SQLException {
        assertEquals(1, TestMapper.count("carports", connectionPool));
        CarportMapper.SaveCarportInDB(expectedcarport.getName(), expectedcarport.getPrice(), expectedcarport.getType(), expectedcarport.getProductionDescription(), expectedcarport.getSpecification().getSpecificationId(), expectedcarport.getPdf(), connectionPool);
        assertEquals(2, TestMapper.count("carports", connectionPool));
    }

    @Test
    public void createUDCTest() throws SQLException, DatabaseException {
        assertEquals(1,TestMapper.count("carports",connectionPool));
        CarportMapper.SaveCarportInDB(expectedUDC.getName(),expectedUDC.getPrice(),expectedUDC.getType(),expectedUDC.getProductionDescription(),expectedUDC.getSpecification().getSpecificationId(),connectionPool);
        assertEquals(2,TestMapper.count("carports",connectionPool));
    }
    @Test
    public void deletCarportTest() throws DatabaseException, SQLException {
        assertEquals(1, TestMapper.count("carports", connectionPool));
        CarportMapper.deleteCarport(1,connectionPool);
        assertEquals(0, TestMapper.count("carports", connectionPool));
    }
}