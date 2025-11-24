package app.entities;

public class UserDefinedCarport extends Carport {
    int carportID;
    String name;
    float price;
    int type;
    String productionDescription;
    int specification;

    public UserDefinedCarport(int carportID, String name, float price, int type, String productionDescription, int specification) {
        super(carportID,name,price,type,productionDescription,specification);
    }
}
