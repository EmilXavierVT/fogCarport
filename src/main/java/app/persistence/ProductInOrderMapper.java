package app.persistence;

import app.entities.Product;
import app.entities.ProductInOrder;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductInOrderMapper
{
    public static void createProductInOrder(int OrderID, Product product, int amount, ConnectionPool connectionPool) throws DatabaseException
    {
        String sql = "INSERT INTO products_in_orders (order_id, product_id, amount) VALUES ( ?, ? , ?)";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {

            ps.setInt(1, OrderID);
            ps.setInt(2, product.getProductID());
            ps.setInt(3, amount);
            ps.executeUpdate();

        } catch (SQLException e)
        {
            throw new DatabaseException("Error creating product in order", e.getMessage());
        }
    }

    public static ProductInOrder getProductInOrderById(int productInOrderId, ConnectionPool connectionPool) throws DatabaseException
    {
        String sql = "SELECT * FROM products_in_orders WHERE products_in_order_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setInt(1, productInOrderId);
            ResultSet rs = ps.executeQuery();

            if (rs.next())
            {
                return new ProductInOrder(
                        rs.getInt("products_in_order_id"),
                        rs.getInt("order_id"),
                        new ProductMapper().getProductByID(rs.getInt("product_id"),connectionPool),
                        rs.getInt("amount")
                );
            }
        } catch (SQLException e)
        {
            throw new DatabaseException("Error getting product in order", e.getMessage());
        }
        return null;
    }

    public static void updateProductInOrder(int productInOrderId, int amount, ConnectionPool connectionPool) throws DatabaseException
    {
        String sql = "UPDATE products_in_orders SET amount = ? WHERE products_in_order_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setInt(1, amount);
            ps.setInt(2, productInOrderId);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected != 1)
            {
                throw new DatabaseException("Failed to update product in order with ID: " + productInOrderId);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error updating product in order", e.getMessage());
        }
    }

    public static void deleteProductInOrder(int productInOrderId, ConnectionPool connectionPool) throws DatabaseException
    {
        String sql = "DELETE FROM products_in_orders WHERE products_in_order_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setInt(1, productInOrderId);
            ps.executeUpdate();
        } catch (SQLException e)
        {
            throw new DatabaseException("Error deleting product in order", e.getMessage());
        }
    }

    public static List<ProductInOrder> getAllProductsInOrderByOrderID(int orderId, ConnectionPool connectionPool) throws DatabaseException
    {
        List<ProductInOrder> productsInOrder = new ArrayList<>();
        String sql = "SELECT * FROM products_in_orders WHERE order_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();

            while (rs.next())
            {
                productsInOrder.add(new ProductInOrder(
                        rs.getInt("products_in_order_id"),
                        rs.getInt("order_id"),
                        new ProductMapper().getProductByID(rs.getInt("product_id"),connectionPool),
                        rs.getInt("amount")
                ));
            }
        } catch (SQLException e)
        {
            throw new DatabaseException("Error getting all products in order", e.getMessage());
        }
        return productsInOrder;
    }

}
