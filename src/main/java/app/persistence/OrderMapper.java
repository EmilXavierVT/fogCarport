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


    public void createOrder(int userId, LocalDate date, int discountId, ConnectionPool connectionPool) throws DatabaseException
    {
        String sql = "INSERT INTO orders ( user_id, date, applied_discount) VALUES (?, ?, ?, ?) RETURNING id";

        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            //ps.setInt(1, orderId);
            ps.setInt(1, userId);
            ps.setDate(2, java.sql.Date.valueOf(date));
            ps.setInt(3, discountId);
            ps.executeQuery();
        } catch (SQLException e)
        {
            throw new DatabaseException("SaveOrder Mapper", e.getMessage());
        }
    }
    // discount not added


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
                int id = rs.getInt("id");
                int userId = rs.getInt("user_id");
                int discountId = rs.getInt("applied_discount");
                LocalDate date = rs.getDate("date").toLocalDate();
                orders.add(new Order(id, userId, date));
            }
        } catch (SQLException e)
        {
            throw new DatabaseException("Error getting all orders", e.getMessage());
        }
        return orders;
    }

    // get oders from last 7 days not implimented


    // maybe we want to look into this later
    public void removeOrder(int orderId, ConnectionPool connectionPool) throws DatabaseException
    {
        try (Connection connection = connectionPool.getConnection())
        {
            String sql = "DELETE FROM orders WHERE order_id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, orderId);
            ps.executeQuery();

        } catch (SQLException e)
        {
            throw new DatabaseException("Error removing order", e.getMessage());
        }
    }
}
