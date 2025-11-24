package app.entities;

public abstract class Carport {
   int carportID;
   String name;
   float price;
   int type;
   String productionDescription;
   int specification;

   Carport(int carportID, String name, float price, int type, String productionDescription, int specification) {
       this.carportID = carportID;
       this.name = name;
       this.price = price;
       this.type = type;
       this.productionDescription = productionDescription;
       this.specification = specification;
   }
}
