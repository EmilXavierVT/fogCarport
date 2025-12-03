package app.entities;


import java.util.ArrayList;
import java.util.List;

public class UserDefinedCarport extends Carport
{
    int carportID;
    String name;
    float price;
    int type;
    String productionDescription;
    Specification specification;
    List itemList = new ArrayList();

    public UserDefinedCarport(int carportID, String name, float price, int type, String productionDescription, Specification specification)
    {
        super(carportID,name,price,type,productionDescription,specification);
    }


}
