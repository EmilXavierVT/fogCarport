package app.persistence;


import app.entities.Specification;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SpecificationMapper
{

    public static void CreateSpecification(int EAN, String model, String roomFor,boolean shed, int post,
                                           int beam, int rafter, int roof, int fasciaBoard, int length, int width, int heightFront,
                                           int heightRear, int roofLength, int roofWidth, int exteriorWidthAtPost, int parkingLength,
                                           int parkingWidth, int shedDepth, int shedWidth,ConnectionPool connectionPool) throws SQLException
    {
        String sql = "INSERT INTO specifications (EAN,model.room_for,shed,post,beam,rafter, roof, " +
                "fascia_board, length, width, height_front, height_rear, roof_length, roof_width," +
                " exterior_width_at_post, parking_length, parking_width,  shed_depth, shed_width)" +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setInt(1, EAN);
            ps.setString(2, model);
            ps.setString(3, roomFor);
            ps.setBoolean(4, shed);
            ps.setInt(5, post);
            ps.setInt(6, beam);
            ps.setInt(7, rafter);
            ps.setInt(8, roof);
            ps.setInt(9, fasciaBoard);
            ps.setInt(10, length);
            ps.setInt(11, width);
            ps.setInt(12, heightFront);
            ps.setInt(13, heightRear);
            ps.setInt(14, roofLength);
            ps.setInt(15, roofWidth);
            ps.setInt(16, exteriorWidthAtPost);
            ps.setInt(17, parkingLength);
            ps.setInt(18, parkingWidth);
            ps.setInt(19, shedDepth);
            ps.setInt(20, shedWidth);
            ps.executeUpdate();
        }
    }

    public static Specification getSpecificationByID(int specificationId,ConnectionPool connectionPool) throws SQLException, DatabaseException
    {
        String sql = "SELECT * FROM specifications WHERE specification_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setInt(1, specificationId);
            ResultSet rs = ps.executeQuery();

            if (rs.next())
            {
                return new Specification(
                        rs.getInt("specification_id"),
                        rs.getInt("EAN"),
                        rs.getString("model"),
                        rs.getString("room_for"),
                        rs.getBoolean("shed"),
                        rs.getInt("post"),
                        rs.getInt("beam"),
                        rs.getInt("rafter"),
                        rs.getInt("roof"),
                        rs.getInt("fascia_board"),
                        rs.getInt("length"),
                        rs.getInt("width"),
                        rs.getInt("height_front"),
                        rs.getInt("height_rear"),
                        rs.getInt("roof_length"),
                        rs.getInt("roof_width"),
                        rs.getInt("exterior_width_at_post"),
                        rs.getInt("parking_length"),
                        rs.getInt("parking_width"),
                        rs.getInt("shed_depth"),
                        rs.getInt("shed_width"));
            } else
            {
                throw new DatabaseException("No Specification found on id " + specificationId);
            }
        } catch (SQLException e)
        {
            throw new DatabaseException("Error retrieving user", e.getMessage());
        }
    }


}
