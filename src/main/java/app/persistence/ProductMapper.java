package app.persistence;

import app.entities.Product;
import app.exceptions.DatabaseException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductMapper
{
    public static void saveProduct(String name, String dimensions, String description, double price, int type, ConnectionPool connectionPool)
    {
        String sql = "INSERT INTO products (name, dimensions, description, price, type) VALUES(?,?,?,?,?)";

        try(Connection connection = connectionPool.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setString(1, name);
            ps.setString(2, dimensions);
            ps.setString(3, description);
            ps.setDouble(4, price);
            ps.setInt(5, type);
            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static Product getProductByID(int productID, ConnectionPool connectionPool) throws DatabaseException, SQLException
    {
        String sql = "SELECT * FROM products WHERE product_id=?";

        try(Connection connection = connectionPool.getConnection();
        PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setInt(1, productID);
            ResultSet rs = ps.executeQuery();

            if(rs.next())
            {
                return new Product(rs.getInt("product_id"),rs.getString("name"),rs.getString("dimensions"),rs.getString("description"),rs.getFloat("price"),rs.getInt("type"));
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static void updateOrder(int ProductID,String name, String dimensions, String description, float price, int type, ConnectionPool connectionPool)
    {
        String sql ="UPDATE products SET name=?, dimensions=?,description=?,price=?,type=? WHERE product_id =?";

        try(Connection connection = connectionPool.getConnection();
        PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setString(1, name);
            ps.setString(2, dimensions);
            ps.setString(3, description);
            ps.setFloat(4, price);
            ps.setInt(5, type);
            ps.setInt(6, ProductID);
            int rowsAffected = ps.executeUpdate();

            if ( rowsAffected != 1 )
            {
                throw new DatabaseException("Failed to update product with ID: " + ProductID);
            }
        }
        catch (SQLException |DatabaseException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static void deleteProduct(int ProductID, ConnectionPool connectionPool) throws DatabaseException, SQLException
    {
        String sql = "DELETE FROM products WHERE product_id=?";
        try(Connection connection = connectionPool.getConnection();
        PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setInt(1, ProductID);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected != 1)
            {
                throw new DatabaseException("Failed to delete product with ID: " + ProductID);
            }
        }
    }

    public static List<Product> getAllProducts(ConnectionPool connectionPool) throws DatabaseException
    {
        String sql = "SELECT * FROM products ORDER BY name ASC";
        List<Product> products = new ArrayList<>();

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ResultSet rs = ps.executeQuery();

            while (rs.next())
            {
                products.add(new Product(
                        rs.getInt("product_id"),
                        rs.getString("name"),
                        rs.getString("dimensions"),
                        rs.getString("description"),
                        rs.getFloat("price"),
                        rs.getInt("type"),
                        rs.getInt("gap"),
                        rs.getInt("min"),
                        rs.getInt("max")));
            }
            return products;
        }
        catch (SQLException e)
        {
            throw new DatabaseException("Error getting all products from database", e.getMessage());
        }
    }

    public static void updateProductPrice(int productId, float newPrice, ConnectionPool connectionPool)
    {
        try
        {
            String sql = "UPDATE products SET price = ? WHERE product_id = ?";
            try (Connection connection = connectionPool.getConnection();
                 PreparedStatement ps = connection.prepareStatement(sql))
            {
                ps.setFloat(1, newPrice);
                ps.setInt(2, productId);
                int rowsAffected = ps.executeUpdate();

                if (rowsAffected != 1)
                {
                    throw new DatabaseException("Failed to update product price for ID: " + productId);
                }
            }
        }
        catch (SQLException | DatabaseException e)
        {
            throw new RuntimeException(e);
        }
    }
}
