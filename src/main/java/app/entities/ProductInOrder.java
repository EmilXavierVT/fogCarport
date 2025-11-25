package app.entities;

import lombok.Data;
@Data

public class ProductInOrder
{
    private int productInOrderID;
    private int orderID;
    private Product product;
    private int amount;

    public ProductInOrder(int productInOrderID, int orderID, Product product, int amount)
    {
        this.productInOrderID = productInOrderID;
        this.orderID = orderID;
        this.product = product;
        this.amount = amount;
    }
}
