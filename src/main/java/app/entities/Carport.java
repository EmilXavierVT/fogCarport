package app.entities;

import lombok.Data;
@Data

public abstract class Carport
{
   int carportID;
   String name;
   float price;
   int type;
   String productionDescription;
   Specification specification;

   Carport(int carportID, String name, float price, int type, String productionDescription, Specification specification)
   {
       this.carportID = carportID;
       this.name = name;
       this.price = price;
       this.type = type;
       this.productionDescription = productionDescription;
       this.specification = specification;
   }

    public Carport(int carportID, String name, float price, int type, String productionDescription) {
        this.carportID = carportID;
        this.name = name;
        this.price = price;
        this.type = type;
        this.productionDescription = productionDescription;
    }
}
