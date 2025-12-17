package app.services;

import app.entities.*;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.ProductMapper;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

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
    private int angle;
    private Product beam;
    private Product post;
    private Product rafter;
    private Product roof;
    private Product fasciaBoard;
    private Product wallCovering;
    private Product bottomScrew;
    private Product holeBand;
    private Product rightFitting;
    private Product leftFitting;
    private Product coveringScrew;
    private Product bolt;
    private Product squareWasher;
    private Product fourSixScrew;
    private Product fourSevenScrew;
    private Product fourFiveScrew;
    private Product handle;
    private Product tHinge;
    private Product angleHinge;
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
    List<Product> bottomScrews = allProducts.stream().filter(product -> product.getType() == 10).toList();
    List<Product> holeBands = allProducts.stream().filter(product -> product.getType() == 11 ).toList();
    List<Product>  rightFittings= allProducts.stream().filter(product -> product.getType() == 12 ).toList();
    List<Product>  leftFittings= allProducts.stream().filter(product -> product.getType() == 12).toList();
    List<Product>  fourSixScrews= allProducts.stream().filter(product -> product.getType() == 13).toList();
    List<Product>  coveringScrews= allProducts.stream().filter(product -> product.getType() == 14).toList();
    List<Product>  bolts = allProducts.stream().filter(product -> product.getType() == 15).toList();
    List<Product> squarewashers = allProducts.stream().filter(product -> product.getType() == 16).toList();
    List<Product> fourSevenScrews = allProducts.stream().filter(product -> product.getType() == 17).toList();
    List<Product> fourFiveScrews = allProducts.stream().filter(product -> product.getType() == 18).toList();
    List<Product>  handles = allProducts.stream().filter(product -> product.getType() == 19).toList();
    List<Product> tHinges = allProducts.stream().filter(product -> product.getType() == 20).toList();
    List<Product> angleHinges = allProducts.stream().filter(product -> product.getType() == 21).toList();

    beam = specification.getBeam();
    post = specification.getPost();
    rafter = specification.getRafter();
    roof = specification.getRoof();
    fasciaBoard= specification.getFasciaBoard();
    wallCovering = wallCoverings.get(0);
    bottomScrew =bottomScrews.get(0);
    holeBand = holeBands.get(0);
    rightFitting = rightFittings.get(0);
    leftFitting = leftFittings.get(0);
    coveringScrew = coveringScrews.get(0);
    bolt = bolts.get(0);
    squareWasher = squarewashers.get(0);
    fourSevenScrew = fourSevenScrews.get(0);
    fourSixScrew =fourSixScrews.get(0);
    fourFiveScrew = fourFiveScrews.get(0);
    handle = handles.get(0);
    tHinge = tHinges.get(0);
    angleHinge = angleHinges.get(0);

    if(specification instanceof AngleSpecification)
    {
      angle = ((AngleSpecification) specification).getAngle();
    }

//    beams
    if(length<=600)
    {
        itemList.add(new ProductInOrder(0,beam,2,length));
    }
    else if(length<750)
    {
        itemList.add(new ProductInOrder(0,beam,2,600));
        itemList.add(new ProductInOrder(0,beam,1,300));
    }
    else
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
        }
        else
        {
            itemList.add(new ProductInOrder(0, roof, amountOfRoof,600));
            itemList.add(new ProductInOrder(0, roof, amountOfRoof,length-600));
        }
//    fasciaBoard one will always be width
        itemList.add(new ProductInOrder(0,fasciaBoard,2,width));
        if(length<600)
        {
            itemList.add(new ProductInOrder(0,fasciaBoard,2,length));
        }
        else if(length<750)
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
//        all Screws
        itemList.add(new ProductInOrder(0,bottomScrew,bottomScrewsAmount,0));
        itemList.add(new ProductInOrder(0,holeBand,holeBandAmount,0));
        itemList.add(new ProductInOrder(0,rightFitting,rightFittingAmount,0));
        itemList.add(new ProductInOrder(0,leftFitting,leftFittingAmount,0));
        itemList.add(new ProductInOrder(0,fourSixScrew,fourSixScrewsAmount,0));
        itemList.add(new ProductInOrder(0, coveringScrew, coveringScrewsAmount, 0));
        itemList.add(new ProductInOrder(0, bolt, boltAmount, 0));
        itemList.add(new ProductInOrder(0, squareWasher, squareWasherAmount, 0));
        if(specification.isShed())
        {
            itemList.add(new ProductInOrder(0, fourSevenScrew, fourSevenScrewsAmount, 0));
            itemList.add(new ProductInOrder(0, fourFiveScrew, fourFiveScrewsAmount, 0));
            itemList.add(new ProductInOrder(0, handle, handleAmount, 0));
            itemList.add(new ProductInOrder(0, tHinge, tHingeAmount, 0));
            itemList.add(new ProductInOrder(0, angleHinge, angleHingeAmount, 0));
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