package app.persistence;


import app.enteties.Specifications;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SpecificationsMapper {

    public static void CreateSpecification(int EAN, String model, String roomFor,boolean shed, int post,
                                           int beam, int rafter, int roof, int fasciaBoard, int length, int width, int heightFront,
                                           int heightRear, int roofLength, int roofWidth, int exteriorWidthAtPost, int parkingWidth,
                                           int shedDepth, int shedWidth) throws SQLException {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        String sql = "INSERT INTO specifications (EAN,model.room_for,shed,post,beam,rafter, roof, " +
                "fascia_board, length, width, height_front, height_rear, roof_length, roof_width," +
                " exterior_width_at_post, parking_length, parking_width, shed_depth, shed_width)" +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
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
            ps.setInt(17, parkingWidth);
            ps.setInt(18, shedDepth);
            ps.setInt(19, shedWidth);
            ps.executeUpdate();
        }
    }

    public static Specifications GetSpecifications(int specificationId) throws SQLException {

    }
}
