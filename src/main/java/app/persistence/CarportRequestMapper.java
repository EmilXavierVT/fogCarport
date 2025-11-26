package app.persistence;

import app.entities.CarportRequest;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CarportRequestMapper
{

    public static void createCarportRequest(int userID, int carportID,int salesRepID, ConnectionPool connectionPool) throws DatabaseException
    {
        String sql = "INSERT INTO carport_requests (user_id, carport_id, sales_rep_id) VALUES (?, ?, ?)";

        try(Connection connection = connectionPool.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);)
        {
        ps.setInt(1, userID);
        ps.setInt(2, carportID);
        ps.setInt(3, salesRepID);
        ps.executeUpdate();
        } catch (SQLException e)
        {
            throw new DatabaseException(e.getMessage()+ "problem with saving carport request");
        }
    }
    public static CarportRequest getCarportbyID(int id, ConnectionPool connectionPool) throws DatabaseException, SQLException
    {
        String sql ="SELECT * FROM carport_requests WHERE carport_request_id = ?";

        try(Connection connection = connectionPool.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            while(rs.next())
            {
                return new CarportRequest(rs.getInt(1),
                        UserMapper.getUserByID(rs.getInt(2),connectionPool),
                        CarportMapper.getCarportByID(rs.getInt(3),connectionPool),
                        UserMapper.getUserByID(rs.getInt(4),connectionPool));
            }
        } catch (SQLException e)
        {
            throw new DatabaseException(e.getMessage()+ "problem with getting a carport request by id: "+id);
        }
        return null;
    }

    public static void updateCarportRequest(int requestId, int userId, int carportId, int salesRepId, ConnectionPool connectionPool) throws DatabaseException
    {
        String sql = "UPDATE carport_requests SET user_id = ?, carport_id = ?, sales_rep_id = ? WHERE carport_request_id = ?";
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);)
        {
            ps.setInt(1, userId);
            ps.setInt(2, carportId);
            ps.setInt(3, salesRepId);
            ps.setInt(4, requestId);
            ps.executeUpdate();
        } catch (SQLException e)
        {
            throw new DatabaseException(e.getMessage() + "problem with updating carport request with id: " + requestId);
        }
    }
    public static void deleteCarportRequest(int id, ConnectionPool connectionPool) throws DatabaseException
    {
        String sql = "DELETE FROM carport_requests WHERE carport_request_id = ?";
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);)
        {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e)
        {
            throw new DatabaseException(e.getMessage() + "problem with deleting carport request with id: " + id);
        }
    }
}
