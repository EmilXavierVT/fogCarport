package app.entities;
import lombok.Data;

@Data
public class ProductInOrder
{
    private int productInOrderID;
    private int orderID;
    private Product product;
    private int amount;
    private int length;
    private float price;

    public ProductInOrder(int productInOrderID, int orderID, Product product, int amount)
    {
        this.productInOrderID = productInOrderID;
        this.orderID = orderID;
        this.product = product;
        this.amount = amount;
        this.price = product.getPrice() * amount;
    }

    public ProductInOrder(int orderID, Product product, int amount, int length) {
        this.orderID = orderID;
        this.product = product;
        this.amount = amount;
        this.length = length;
        this.price = product.getPrice() * amount;
    }
}
