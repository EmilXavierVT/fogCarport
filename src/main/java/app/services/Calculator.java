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
public class Calculator
{
    private Specification specification;
    private int width;
    private int length;
    private int amountOfPosts = 0;
    private int amountOfBeams;
    private int amountOfRafters;
    private int amountOfRoof;
    private int amountOfWallCovering;
    private int lengthOfFasciaBoard;
    private int bottomScrewsAmount;
    private int holeBandAmount;
    private int rightFittingAmount;
    private int leftFittingAmount;
    private int fourSixScrewsAmount;
    private int coveringScrewsAmount;
    private int boltAmount;
    private int squareWasherAmount;
    private int fourSevenScrewsAmount;
    private int fourFiveScrewsAmount;
    private int handleAmount;
    private int tHingeAmount;
    private int angleHingeAmount;
    private Product beam;
    private Product post;
    private Product rafter;
    private Product roof;
    private Product fasciaBoard;
    private Product wallCovering;
    ConnectionPool connectionPool = ConnectionPool.getInstance();

    public Calculator( Specification specification)
    {
        this.specification = specification;
        this.width = specification.getWidth();
        this.length = specification.getLength();
        calcPost();
        calcBeams();
        calcRafters();
        calcRoof();
        calcFasciaBoard();
        calcAmountOfWallCovering();
        calcScrews();
    }

    public void calcPost()
    {
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

    public void calcBeams() {amountOfBeams = (length < 600) ? 6:4;}

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

    private void calcAmountOfWallCovering()
    {
       amountOfWallCovering = ((specification.getShedDepth() * 2 + specification.getShedWidth() * 2) / 8);
    }

    private void calcScrews()
    {
        bottomScrewsAmount = amountOfRoof/2;
        rightFittingAmount = amountOfRafters;
        leftFittingAmount = amountOfRafters;
        fourSixScrewsAmount = 1;
        coveringScrewsAmount = amountOfRafters/5;
        boltAmount = amountOfRafters/2 + amountOfPosts;
        squareWasherAmount = amountOfPosts +1;
        fourSevenScrewsAmount = amountOfWallCovering/100;
        fourFiveScrewsAmount = amountOfWallCovering/100;
        handleAmount = (amountOfWallCovering > 0) ? 1 : 0;
        tHingeAmount = ((amountOfWallCovering > 0) ? 2 : 0);
        angleHingeAmount = (amountOfWallCovering > 0) ? 35 : 0;
    }


    public List<ProductInOrder> setItemList() throws DatabaseException
    {
        List<ProductInOrder> itemList = new ArrayList<>();
        List<Product> allProducts = ProductMapper.getAllProducts(connectionPool);

    List<Product> wallCoverings = allProducts.stream().filter(product -> product.getType() == 9).toList();

    beam = specification.getBeam();
    post = specification.getPost();
    rafter = specification.getRafter();
    roof = specification.getRoof();
    fasciaBoard= specification.getFasciaBoard();
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
        if(specification.isShed())
        {
            int amountOfPlanks = (int) Math.ceil((double) specification.getShedWidth()/8)*2 + (int) Math.ceil((double) specification.getShedDepth()/8)*2;
            itemList.add(new ProductInOrder(0, wallCovering, amountOfPlanks, 0));
            itemList.add(new ProductInOrder(0, post, 3, 0));
        }

        return itemList;
    }

    public double getCostPrice() throws DatabaseException
    {
        double totalCost = 0;

         for(ProductInOrder productInOrder : setItemList())
         {
             totalCost += (productInOrder.getProduct().getPrice()*(double)productInOrder.getAmount());

         }
        return totalCost;
    }



}