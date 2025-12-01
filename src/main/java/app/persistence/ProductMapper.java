package app.persistence;

import app.entities.Product;
import app.exceptions.DatabaseException;

import java.sql.*;

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

        } catch (SQLException e)
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
        } catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static void updateOrder(int orderID,String name, String dimensions, String description, float price, int type, ConnectionPool connectionPool)
    {
        String sql ="UPDATE orders SET name=?, dimensions=?,description=?,price=?,type=? WHERE id=?";

        try(Connection connection = connectionPool.getConnection();
        PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setString(1, name);
            ps.setString(2, dimensions);
            ps.setString(3, description);
            ps.setFloat(4, price);
            ps.setInt(5, type);
            ps.setInt(6, orderID);
            int rowsAffected = ps.executeUpdate();

            if ( rowsAffected != 1 )
            {
                throw new DatabaseException("Failed to update user with ID: " + orderID);
            }
        } catch (SQLException |DatabaseException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static void deleteOrder(int orderID, ConnectionPool connectionPool) throws DatabaseException, SQLException
    {
        String sql = "DELETE FROM orders WHERE id=?";
        try(Connection connection = connectionPool.getConnection();
        PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setInt(1, orderID);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected != 1)
            {
                throw new DatabaseException("Failed to delete user with ID: " + orderID);
            }
        }
    }
}
