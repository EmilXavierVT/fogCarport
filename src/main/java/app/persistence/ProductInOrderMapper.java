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

public class ProductInOrderMapper {

    public void createProductInOrder(int orderId, Product product, int amount) throws DatabaseException {
        String sql = "INSERT INTO product_in_order (order_id, product_id, amount) VALUES (?, ?, ?)";
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.setInt(2, product.getProductID());
            ps.setInt(3, amount);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error creating product in order", e.getMessage());
        }
    }

    public ProductInOrder getProductInOrderById(int productInOrderId) throws DatabaseException {
        String sql = "SELECT * FROM product_in_order WHERE product_in_order_id = ?";
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, productInOrderId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new ProductInOrder(
                        rs.getInt("product_in_order_id"),
                        rs.getInt("order_id"),
                        new ProductMapper().getProductByID(rs.getInt("product_id")),
                        rs.getInt("amount")
                );
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error getting product in order", e.getMessage());
        }
        return null;
    }

    public void updateProductInOrder(int productInOrderId, int amount) throws DatabaseException {
        String sql = "UPDATE product_in_order SET amount = ? WHERE product_in_order_id = ?";
        ConnectionPool connectionPool = ConnectionPool.getInstance();

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, amount);
            ps.setInt(2, productInOrderId);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected != 1) {
                throw new DatabaseException("Failed to update product in order with ID: " + productInOrderId);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error updating product in order", e.getMessage());
        }
    }

    public void deleteProductInOrder(int productInOrderId) throws DatabaseException {
        String sql = "DELETE FROM product_in_order WHERE product_in_order_id = ?";
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, productInOrderId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error deleting product in order", e.getMessage());
        }
    }

    public List<ProductInOrder> getAllProductsInOrder(int orderId) throws DatabaseException {
        List<ProductInOrder> productsInOrder = new ArrayList<>();
        String sql = "SELECT * FROM product_in_order WHERE order_id = ?";
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                productsInOrder.add(new ProductInOrder(
                        rs.getInt("product_in_order_id"),
                        rs.getInt("order_id"),
                        new ProductMapper().getProductByID(rs.getInt("product_id")),
                        rs.getInt("amount")
                ));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error getting all products in order", e.getMessage());
        }
        return productsInOrder;
    }
}
