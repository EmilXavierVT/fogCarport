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

public class CarportRequestMapperTest {
    private final static Dotenv dotenv = Dotenv.load();
    private final static String USER = dotenv.get("DB-USER");
    private final static String PASSWORD = dotenv.get("DB-PASSWORD");
    private final static String URL = dotenv.get("DB-URL");
    private final static String DB = "carport";

    static ConnectionPool connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);

    User tempUser = new User(1,"Emil","Thorsen",2200,"Farumgade",1,"2th","ex@tv.dk","1234",0);
    User tempSalesRep = new User(2,"Frederik","Edvardsen",2450,"Egetoftevej",11,"","fred@dk.dk","1234",1);
    User tempSalesRep2 = new User(3,"Frederik","Edvardsen",2450,"Egetoftevej",11,"","fred@dk.dk","1234",1);

    Product post = new Product(22, "trykimprægneret stolpe", "125x125 mm", "stolper graves 90 cm i jord", 110, 22);
    Product beam = new Product(3, "spærtræ", "45x195 mm", "Remme i sider, sadles ned i stolper", 100, 3);
    Product rafter = new Product(26, "spærtræ færdigsamlede 25 gr", "145x145 mm", "spær monteres på remme", 70, 3);
    Product roof = new Product(28, "B&C dobbelt-S sort beton tagsten m/ 30 års garant", "0", "tag", 50, 23);
    Product fasciaBoard = new Product(1, "trykimp. Bræt", "25x200 mm", "understernbrædder til for- & bagende", 15, 1);

    Specification temp = new Specification(1, 2000000710761L, "Carport", 2, true,
            post, beam, rafter, roof, fasciaBoard, 780, 600, 380,
            380, 780, 600, 530, 540, 505,
            210, 530);

    StandardCarport tempCarport = new StandardCarport(1, "CARPORT CP02HUR DOBBELT 6,0X7,8M MED REDSKABSRUM 2,1X5,1M HØJ REJSNING", 54998, 2, "Bred byg selv-carport 6,00 x 7,80 m. med høj rejsning og plads til 2 biler Stort redskabsrum bagerst i carport Carporten er åben på begge sider, men kan lukkes efter ønske. Leveres med trykimprægnerede stolper, stern og vindskeder, færdigsamlede spær direkte fra fabrik, lodret beklædning på gavle og skur, samt BogC sort dobbelt-S til tagbeklædning. Alle skruer, bolte og beslag, samt udførlig byggevejledning medfølger. Carporten kan også oplevelses i Fog Helsinge. NB! Leveres som Byg-selv sæt - usamlet og ubehandlet. Passer målene på denne carport ikke med dine ønsker? Vi kan skræddersy en carport på specialmål til dig. Klik på linket Bestil tilbud på carport i specialmål, som du finder under Links og dokumenter nedenfor.", temp, "2000000710761.pdf");
    CarportRequest expectedCarportRequest = new CarportRequest(1,tempUser,tempCarport,tempSalesRep);

    @BeforeAll
    public static void setUpClass()
    {

        try (Connection testConnection = connectionPool.getConnection())
        {
            try (Statement stmt = testConnection.createStatement())
            {
                // The test schema is already created, so we only need to delete/create test tables
                stmt.execute("DROP TABLE IF EXISTS test_schema.carport_requests");
                stmt.execute("DROP TABLE IF EXISTS test_schema.carports");
                stmt.execute("DROP TABLE IF EXISTS test_schema.specifications");
                stmt.execute("DROP TABLE IF EXISTS test_schema.products");
                stmt.execute("DROP TABLE IF EXISTS test_schema.users");

                stmt.execute("DROP SEQUENCE IF EXISTS test_schema.carport_requests_carport_request_id_seq CASCADE;");
                stmt.execute("DROP SEQUENCE IF EXISTS test_schema.carports_carport_id_seq CASCADE");
                stmt.execute("DROP SEQUENCE IF EXISTS test_schema.specifications_specification_id_seq CASCADE");
                stmt.execute("DROP SEQUENCE IF EXISTS test_schema.products_product_id_seq CASCADE");
                stmt.execute("DROP SEQUENCE IF EXISTS test_schema.users_user_id_seq CASCADE;");

                // Create tables as copy of original public schema structure
                stmt.execute("CREATE TABLE test_schema.carport_requests AS (SELECT * from public.carport_requests) WITH NO DATA");
                stmt.execute("CREATE TABLE test_schema.carports (LIKE public.carports INCLUDING ALL)");
                stmt.execute("CREATE TABLE test_schema.specifications (LIKE public.specifications INCLUDING ALL)");
                stmt.execute("CREATE TABLE test_schema.products (LIKE public.products INCLUDING ALL)");
                stmt.execute("CREATE TABLE test_schema.users AS (SELECT * from public.users) WITH NO DATA");


                // Create sequences for auto generating id's for members and sports
                stmt.execute("CREATE SEQUENCE test_schema.carport_requests_carport_request_id_seq");
                stmt.execute("CREATE SEQUENCE test_schema.carports_carport_id_seq");
                stmt.execute("CREATE SEQUENCE test_schema.specifications_specification_id_seq");
                stmt.execute("CREATE SEQUENCE test_schema.products_product_id_seq");
                stmt.execute("CREATE SEQUENCE test_schema.users_user_id_seq");


                stmt.execute("ALTER TABLE test_schema.carport_requests ALTER COLUMN carport_request_id SET DEFAULT nextval('test_schema.carport_requests_carport_request_id_seq')");
                stmt.execute("ALTER TABLE test_schema.carports ALTER COLUMN carport_id SET DEFAULT nextval('test_schema.carports_carport_id_seq')");
                stmt.execute("ALTER TABLE test_schema.specifications ALTER COLUMN specification_id SET DEFAULT nextval('test_schema.specifications_specification_id_seq')");
                stmt.execute("ALTER TABLE test_schema.products ALTER COLUMN product_id SET DEFAULT nextval('test_schema.products_product_id_seq')");
                stmt.execute("ALTER TABLE test_schema.users ALTER COLUMN user_id SET DEFAULT nextval('test_schema.users_user_id_seq')");

            }
        } catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() throws SQLException {
        try (Connection testConnection = connectionPool.getConnection()) {
            try (Statement stmt = testConnection.createStatement()) {
                // Remove all rows from all tables
                stmt.execute("DELETE FROM test_schema.carport_requests");
                stmt.execute("DELETE FROM test_schema.carports");
                stmt.execute("DELETE FROM test_schema.specifications");
                stmt.execute("DELETE FROM test_schema.products");
                stmt.execute("DELETE FROM test_schema.users");


                // Reset the sequence number
                stmt.execute("SELECT setval('test_schema.carport_requests_carport_request_id_seq', 1)");
                stmt.execute("SELECT setval('test_schema.carports_carport_id_seq', 1)");
                stmt.execute("SELECT setval('test_schema.specifications_specification_id_seq', 1)");
                stmt.execute("SELECT setval('test_schema.products_product_id_seq',1)");
                stmt.execute("SELECT setval('test_schema.users_user_id_seq', 1)");

                // Insert rows
                stmt.execute("INSERT INTO test_schema.carport_requests VALUES " +
                        "(1,1,1,2)");

                //Carport 1
                stmt.execute("INSERT INTO test_schema.carports VALUES " +
                        "(1,'CARPORT CP02HUR DOBBELT 6,0X7,8M MED REDSKABSRUM 2,1X5,1M HØJ REJSNING',54998,2,'Bred byg selv-carport 6,00 x 7,80 m. med høj rejsning og plads til 2 biler Stort redskabsrum bagerst i carport Carporten er åben på begge sider, men kan lukkes efter ønske. Leveres med trykimprægnerede stolper, stern og vindskeder, færdigsamlede spær direkte fra fabrik, lodret beklædning på gavle og skur, samt BogC sort dobbelt-S til tagbeklædning. Alle skruer, bolte og beslag, samt udførlig byggevejledning medfølger. Carporten kan også oplevelses i Fog Helsinge. NB! Leveres som Byg-selv sæt - usamlet og ubehandlet. Passer målene på denne carport ikke med dine ønsker? Vi kan skræddersy en carport på specialmål til dig. Klik på linket Bestil tilbud på carport i specialmål, som du finder under Links og dokumenter nedenfor.',1,'2000000710761.pdf')");

                //specification 1
                stmt.execute("INSERT INTO test_schema.specifications VALUES " +
                        "(1,2000000710761,'Carport',2,true,22,3,26,28,1,780,600,380,380,780,600,530,540,505,210,530)");

                //products
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

                //users
                stmt.execute("INSERT INTO test_schema.users VALUES " +
                        "(1,'Emil','Thorsen',2200,'Farumgade',1,'2th','ex@tv.dk','1234',0)");

                stmt.execute("INSERT INTO test_schema.users VALUES " +
                        "(2,'Frederik','Edvardsen',2450,'Egetoftevej',11,'','fred@dk.dk','1234',1)");
                stmt.execute("INSERT INTO test_schema.users VALUES " +
                        "(3,'Frederik','Edvardsen',2450,'Egetoftevej',11,'','fred@dk.dk','1234',1)");

                // Set sequence to continue from the largest member_id
                stmt.execute("SELECT setval('test_schema.carport_requests_carport_request_id_seq', COALESCE((SELECT MAX(carport_request_id)+1 FROM test_schema.carport_requests), 1), false)");
                stmt.execute("SELECT setval('test_schema.carports_carport_id_seq', COALESCE((SELECT MAX (carport_id)+1 FROM test_schema.carports), 1), false)");
                stmt.execute("SELECT setval('test_schema.specifications_specification_id_seq', COALESCE((SELECT MAX (specification_id)+1 FROM test_schema.specifications), 1), false)");
                stmt.execute("SELECT setval('test_schema.users_user_id_seq', COALESCE((SELECT MAX(user_id)+1 FROM test_schema.users), 1), false)");

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Test
    public void findCarportRequestTest() throws SQLException, DatabaseException {
        assertEquals(expectedCarportRequest,CarportRequestMapper.getCarportbyRequestID(1,connectionPool));
    }

    @Test
    public void CreateCarportRequestTest() throws SQLException, DatabaseException {
        assertEquals(1,TestMapper.count("carport_requests",connectionPool));
        CarportRequestMapper.createCarportRequest(1,1,1,connectionPool);
        assertEquals(2,TestMapper.count("carport_requests",connectionPool));
    }

    @Test
    public void UpdateCarportRequestTest() throws SQLException, DatabaseException {
        assertEquals(expectedCarportRequest,CarportRequestMapper.getCarportbyRequestID(1,connectionPool));
        CarportRequestMapper.updateCarportRequest(1,1,1,3,connectionPool);
        CarportRequest updatedCarportRequest = new CarportRequest(1,tempUser,tempCarport,tempSalesRep2);
        assertEquals(updatedCarportRequest,CarportRequestMapper.getCarportbyRequestID(1,connectionPool));
    }

    @Test
    public void deleteCarportRequestTest() throws SQLException, DatabaseException {
        assertEquals(1,TestMapper.count("carport_requests",connectionPool));
        CarportRequestMapper.deleteCarportRequest(1,connectionPool);
        assertEquals(0,TestMapper.count("carport_requests",connectionPool));
    }
}
