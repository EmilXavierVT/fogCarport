package app.services;

import app.entities.Product;
import app.entities.ProductInOrder;
import app.entities.Specification;
import app.entities.UserDefinedCarport;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.ProductInOrderMapper;
import app.persistence.ProductMapper;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class Calculator {

    private Specification specification;
    private int width;
    private int length;
    private int amountOfPosts = 0;
    private int amountOfBeams;
    private int amountOfRafters;
    private int amountOfRoof;
    private int lengthOfFasciaBoard;
    private Product beam;
    private Product post;
    private Product rafter;
    private Product roof;
    private Product fasciaBoard;
    private Product wallCovering;

    ConnectionPool connectionPool = ConnectionPool.getInstance();

    public Calculator( Specification specification) {
        this.specification = specification;
        this.width = specification.getWidth();
        this.length = specification.getLength();
        calcPost();
        calcBeams();
        calcRafters();
        calcRoof();
        calcFasciaBoard();
    }

//public void calcPostv2()
//{
//    int tmplength = (length-130)/310;
//    int actualpost= (tmplength*2)+4;
//}

    public void calcPost() {

        if (length > 440 && length < 750)
        {
            amountOfPosts = 6;
        }
        else if (length > 750)
        {
            amountOfPosts= 8;
        }
        else
        {
            amountOfPosts = 4;
        }
    }

    public void calcBeams()
    {
        if (length < 600 )
        {
            amountOfBeams = 4;
        }
        else
        {
            amountOfBeams = 6;
        }
    }

    public void calcRafters()
    {
        amountOfRafters = (int)(length/59.5);
    }

    public void calcRoof()
    {
        amountOfRoof = width/100;
    }

    public void calcFasciaBoard()
    {
        lengthOfFasciaBoard = length*2+width;
    }

    public List<ProductInOrder> setItemList() throws DatabaseException {
        List<ProductInOrder> itemList = new ArrayList<>();



        List<Product> allProducts = ProductMapper.getAllProducts(connectionPool);

    List<Product> beams = allProducts.stream().filter(product -> product.getType() == 3).toList();
    List<Product> posts = allProducts.stream().filter(product -> product.getType() == 4).toList();
    List<Product> rafters = allProducts.stream().filter(product -> product.getType() == 3).toList();
    List<Product> roofs = allProducts.stream().filter(product -> product.getType() == 6).toList();
    List<Product> fasciaBoards = allProducts.stream().filter(product -> product.getType() == 1 && product.getType()==2).toList();
    List<Product> wallCoverings = allProducts.stream().filter(product -> product.getType() == 9).toList();

    beam = beams.get(0);
    post = posts.get(0);
    rafter = rafters.get(0);
    roof = roofs.get(0);
    fasciaBoard= fasciaBoards.get(0);
    wallCovering = wallCoverings.get(0);

//    beams
    if(length<=600)
    {
        itemList.add(new ProductInOrder(0,beam,2,length));
    }
    else if(length<750)
    {
        itemList.add(new ProductInOrder(0,beam,2,600));
        itemList.add(new ProductInOrder(0,beam,1,300));
    }else
    {
        itemList.add(new ProductInOrder(0,beam,2,600));
        itemList.add(new ProductInOrder(0,beam,1,360));
    }

//    post
        itemList.add(new ProductInOrder(0,post,amountOfPosts,0));

//    rafters
        itemList.add(new ProductInOrder(0,rafter,amountOfRafters,width));

//    roof
        if(length<600)
        {
            itemList.add(new ProductInOrder(0, roof, amountOfRoof, length));
        } else
        {
            itemList.add(new ProductInOrder(0, roof, amountOfRoof,600));
            itemList.add(new ProductInOrder(0, roof, amountOfRoof,length-600));
        }
//    fasciaBoard one will alwayls be width

        itemList.add(new ProductInOrder(0,fasciaBoard,1,width));
        if(length<600)
        {
            itemList.add(new ProductInOrder(0,fasciaBoard,2,length));
        } else if(length<750)
        {
            itemList.add(new ProductInOrder(0,fasciaBoard,2,600));
            itemList.add(new ProductInOrder(0,fasciaBoard,1,300));
        }else
        {
            itemList.add(new ProductInOrder(0,fasciaBoard,2,600));
            itemList.add(new ProductInOrder(0,fasciaBoard,1,360));
        }





//    All shed calculations
        if(specification.isShed()) {
            int amountOfPlanks = (int) Math.ceil((double) specification.getShedWidth()/8)*2 + (int) Math.ceil((double) specification.getShedDepth()/8)*2;
            itemList.add(new ProductInOrder(0, wallCovering, amountOfPlanks, 0));
            itemList.add(new ProductInOrder(0, post, 3, 0));
        }

        return itemList;
    }




    public double getCostPrice() throws DatabaseException {
        double totalCost = 0;

         for(ProductInOrder productInOrder : setItemList())
         {
             totalCost += (productInOrder.getProduct().getPrice()*(double)productInOrder.getAmount());

         }
        return totalCost;
    }


}