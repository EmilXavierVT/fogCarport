package app.services;

import app.entities.AngleSpecification;
import app.entities.Product;
import app.entities.Specification;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.ProductMapper;
import app.persistence.SpecificationMapper;

import java.sql.SQLException;
import java.util.List;

public class SpecificationWizard {

    public static Specification makeASpecification(int width, int length, boolean roof, int shedWidth, int shedDepth) throws DatabaseException, SQLException {

        int roomFor = (int) (width/100)/3;
        boolean shed = (shedWidth > 0 && shedDepth > 0);

        List<Product> allProducts = ProductMapper.getAllProducts(ConnectionPool.getInstance());

    List<Product> beams = allProducts.stream().filter(product -> product.getProductID() == 3).toList();
    List<Product> posts = allProducts.stream().filter(product -> product.getProductID() ==4).toList();
    List<Product> rafters = allProducts.stream().filter(product -> product.getProductID()==23).toList();
    List<Product> roofs = allProducts.stream().filter(product -> product.getProductID() == 6).toList();
    List<Product> fasciaBoards = allProducts.stream().filter(product -> product.getProductID() == 1).toList();


        Specification actual = SpecificationMapper.createAndGetSpecification(0,"custom",roomFor,shed,
                posts.get(0).getProductID(),beams.get(0).getProductID(),rafters.get(0).getProductID(),roofs.get(0).getProductID(),fasciaBoards.get(0).getProductID(),
                length,width,380,380,length,width,310,
                length-shedDepth,width-30,shedDepth,shedWidth,ConnectionPool.getInstance());

        return actual;
    }
  public static Specification makeAngleSpecification(int width, int length, boolean roof, int shedWidth, int shedDepth, int angle) throws DatabaseException {

    int roomFor = (int) (width/100)/3;
    boolean shed = (shedWidth > 0 && shedDepth > 0);

    List<Product> allProducts = ProductMapper.getAllProducts(ConnectionPool.getInstance());

    List<Product> beams = allProducts.stream().filter(product -> product.getProductID() == 3).toList();
    List<Product> posts = allProducts.stream().filter(product -> product.getProductID() ==4).toList();
    List<Product> rafters = allProducts.stream().filter(product -> product.getProductID()==23).toList();
    List<Product> roofs = allProducts.stream().filter(product -> product.getProductID() == 6).toList();
    List<Product> fasciaBoards = allProducts.stream().filter(product -> product.getProductID() == 1).toList();


    Specification actual = new AngleSpecification(0,"custom",roomFor,shed,
            posts.get(0),beams.get(0),rafters.get(0),roofs.get(0),fasciaBoards.get(0),
            length,width,380,380,length,width,310,

            length-shedDepth,width-30,shedDepth,shedWidth,angle);

    return actual;
}
}

