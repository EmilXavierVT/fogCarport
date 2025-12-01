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
    private int gap;
    private int max;
    private int min;

public Product(int productID, String name, String dimensions, String description, float price, int type)
{
    this.productID = productID;
    this.name = name;
    this.dimensions = dimensions;
    this.description = description;
    this.price = price;
    this.type = type;
}

    public Product(int productID, String name, String dimensions, String description, float price, int type, int gap, int max, int min) {
        this.productID = productID;
        this.name = name;
        this.dimensions = dimensions;
        this.description = description;
        this.price = price;
        this.type = type;
        this.gap = gap;
        this.max = max;
        this.min = min;
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

    public int getGap() {return gap;}

    public void setGap(int gap) {this.gap = gap;}

    public int getMax() {return max;}

    public void setMax(int max) {this.max = max;}

    public int getMin() {return min;}

    public void setMin(int min) {this.min = min;}
}
