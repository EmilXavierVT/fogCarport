package app.entities;

import lombok.Data;
@Data

public class Product
{
    private int productID;
    private String name;
    private String dimensions;
    private String description;
    private float price;
    private int type;

public Product(int productID, String name, String dimensions, String description, float price, int type)
{
    this.productID = productID;
    this.name = name;
    this.dimensions = dimensions;
    this.description = description;
    this.price = price;
    this.type = type;
}

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDimensions() {
        return dimensions;
    }

    public void setDimensions(String dimensions) {
        this.dimensions = dimensions;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
