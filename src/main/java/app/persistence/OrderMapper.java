package app.persistence;

import app.entities.Order;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrderMapper {

    public int getAvailableOrderId(ConnectionPool connectionPool) throws DatabaseException
    {
        int orderId = 0;
        ConnectionPool.getInstance();
        String sql = "SELECT nextval('orders_id_seq')";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ResultSet rs = ps.executeQuery();

            while (rs.next())
            {
                orderId = rs.getInt(1);
            }
        } catch (SQLException e)
        {
            throw new DatabaseException("getAvailableOrderid OrderMapper", e.getMessage());
        }
        return orderId;
    }
    public static void saveOrder(int userID, LocalDate localDate, ConnectionPool connectionPool) throws DatabaseException, SQLException
    {
        String sql = "INSERT INTO orders (user_id, date) VALUES (?, ?)";

        try(Connection connection = connectionPool.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setInt(1, userID);
            ps.setDate(2, java.sql.Date.valueOf(localDate));
            ps.executeUpdate();
        }
    }

    public static Order getOrderByID(int orderID, ConnectionPool connectionPool)
    {
        String sql = "SELECT * FROM orders WHERE order_id = ?";

        try(Connection connection = connectionPool.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setInt(1, orderID);
            ResultSet rs = ps.executeQuery();

            if (rs.next())
            {
                return new Order(rs.getInt("order_id"),UserMapper.getUserByID(rs.getInt("user_id"),connectionPool), rs.getDate("date").toLocalDate());
            }
        } catch (SQLException | DatabaseException e)
        {
            throw new RuntimeException(e);
        }
        return null;
    }



    // get oders from last 7 days not implimented


    public static void updateOrder (int OrderID, LocalDate localDate, ConnectionPool connectionPool) throws DatabaseException, SQLException
    {
        String  sql = "UPDATE orders SET date=? WHERE id=?";

        try(Connection connection = connectionPool.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setDate(1, java.sql.Date.valueOf(localDate));
            int rowsAffected = ps.executeUpdate();

            if ( rowsAffected != 1 )
            {
                throw new DatabaseException("Failed to update user with ID: " + OrderID);
            }
        }
    }

    // maybe we want to look into this later
    public void removeOrder(int orderId, ConnectionPool connectionPool) throws DatabaseException
    {
        String sql = "DELETE FROM orders WHERE order_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setInt(1, orderId);
            ps.executeQuery();

        } catch (SQLException e)
        {
            throw new DatabaseException("Error removing order", e.getMessage());
        }
    }

    public List<Order> getAllOrders(ConnectionPool connectionPool) throws DatabaseException
    {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ResultSet rs = ps.executeQuery();

            while (rs.next())
            {
                orders.add(new Order(rs.getInt("id"), UserMapper.getUserByID(rs.getInt("user_id"),connectionPool), rs.getDate("date").toLocalDate()));
            }
        } catch (SQLException e)
        {
            throw new DatabaseException("Error getting all orders", e.getMessage());
        }
        return orders;
    }
}
