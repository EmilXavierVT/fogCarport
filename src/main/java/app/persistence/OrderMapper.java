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

public class OrderMapper
{
    public static int getAvailableOrderId(ConnectionPool connectionPool) throws DatabaseException
    {
        int orderId = 0;
        ConnectionPool.getInstance();
        String sql = "SELECT nextval('orders_order_id_seq')";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ResultSet rs = ps.executeQuery();

            while (rs.next())
            {
                orderId = rs.getInt(1);
            }
        }
        catch (SQLException e)
        {
            throw new DatabaseException("Error in getAvailableOrderid OrderMapper", e.getMessage());
        }
        return orderId;
    }

    public static Order saveOrderAndReturn(int userID, LocalDate localDate, ConnectionPool connectionPool) throws DatabaseException, SQLException
    {
        int orderId = getAvailableOrderId(connectionPool);
        String sql = "INSERT INTO orders (order_id, user_id, date) VALUES (?, ?, ?)";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setInt(1, orderId);
            ps.setInt(2, userID);
            ps.setDate(3, java.sql.Date.valueOf(localDate));
            ps.executeUpdate();
            return getOrderByID(orderId, connectionPool);
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
        }
        catch (SQLException | DatabaseException e)
        {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static void updateOrder (int OrderID, LocalDate localDate, ConnectionPool connectionPool) throws DatabaseException, SQLException
    {
        String  sql = "UPDATE orders SET date=? WHERE order_id=?";

        try(Connection connection = connectionPool.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setDate(1, java.sql.Date.valueOf(localDate));
            ps.setInt(2, OrderID);
            int rowsAffected = ps.executeUpdate();

            if ( rowsAffected != 1 )
            {
                throw new DatabaseException("Failed to update order with ID: " + OrderID);
            }
        }
    }

    public static void deleteOrder(int orderId, ConnectionPool connectionPool) throws DatabaseException
    {
        String sql = "DELETE FROM orders WHERE order_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setInt(1, orderId);
            ps.executeUpdate();

        }
        catch (SQLException e)
        {
            throw new DatabaseException("Error removing order", e.getMessage());
        }
    }

    public static List<Order> getAllOrders(ConnectionPool connectionPool) throws DatabaseException
    {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ResultSet rs = ps.executeQuery();

            while (rs.next())
            {
                orders.add(new Order(rs.getInt("order_id"), UserMapper.getUserByID(rs.getInt("user_id"),connectionPool), rs.getDate("date").toLocalDate()));
            }
        }
        catch (SQLException e)
        {
            throw new DatabaseException("Error getting all orders", e.getMessage());
        }
        return orders;
    }
}
