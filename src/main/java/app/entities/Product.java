package app.entities;

public class Product {
    private int productID;
    private String name;
    private String dimensions;
    private String description;
    private float price;
    private int type;

public Product(int productID, String name, String dimensions, String description, float price, int type) {
    this.productID = productID;
    this.name = name;
    this.dimensions = dimensions;
    this.description = description;
    this.price = price;
    this.type = type;
}
}
