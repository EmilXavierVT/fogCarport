package app.persistence;

import app.entities.Specification;
import app.exceptions.DatabaseException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SpecificationMapper
{
    public static void createSpecification(long EAN, String model, int roomFor,boolean shed, int post,
                                           int beam, int rafter, int roof, int fasciaBoard, int length, int width, int heightFront,
                                           int heightRear, int roofLength, int roofWidth, int exteriorWidthAtPost, int parkingLength,
                                           int parkingWidth, int shedDepth, int shedWidth,ConnectionPool connectionPool) throws SQLException
    {
        String sql = "INSERT INTO specifications (EAN,model,room_for,shed,post,beam,rafter, roof, " +
                "fascia_board, length, width, height_front, height_rear, roof_length, roof_width," +
                " exterior_width_at_post, parking_length, parking_width,  shed_depth, shed_width)" +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setLong(1, EAN);
            ps.setString(2, model);
            ps.setInt(3, roomFor);
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
                        rs.getLong("EAN"),
                        rs.getString("model"),
                        rs.getInt("room_for"),
                        rs.getBoolean("shed"),
                        ProductMapper.getProductByID(rs.getInt("post"),connectionPool),
                        ProductMapper.getProductByID(rs.getInt("beam"),connectionPool),
                        ProductMapper.getProductByID(rs.getInt("rafter"),connectionPool),
                        ProductMapper.getProductByID(rs.getInt("roof"),connectionPool),
                        ProductMapper.getProductByID(rs.getInt("fascia_board"),connectionPool),
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
            }
            else
            {
                throw new DatabaseException("No Specification found on id " + specificationId);
            }
        }
        catch (SQLException e)
        {
            throw new DatabaseException("Error retrieving user", e.getMessage());
        }
    }

    public static void updateSpecification(int SpecificationID, long EAN, String model, int roomFor,boolean shed, int post,
                                           int beam, int rafter, int roof, int fasciaBoard, int length, int width, int heightFront,
                                           int heightRear, int roofLength, int roofWidth, int exteriorWidthAtPost, int parkingLength,
                                           int parkingWidth, int shedDepth, int shedWidth,ConnectionPool connectionPool) throws SQLException
    {
        String sql = "UPDATE specifications SET EAN = ?,model = ?,room_for = ?,shed = ?,post = ?,beam = ?,rafter = ?, roof = ?," +
                                 "fascia_board = ?, length = ?, width = ?, height_front = ?, height_rear = ?, roof_length = ?, roof_width = ?," +
                                 " exterior_width_at_post = ?, parking_length = ?, parking_width = ?,  shed_depth = ?, shed_width = ? WHERE specification_id = ?";

        try(Connection connection = connectionPool.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setLong(1,EAN);
            ps.setString(2,model);
            ps.setInt(3,roomFor);
            ps.setBoolean(4,shed);
            ps.setInt(5,post);
            ps.setInt(6,beam);
            ps.setInt(7,rafter);
            ps.setInt(8,roof);
            ps.setInt(9,fasciaBoard);
            ps.setInt(10,length);
            ps.setInt(11,width);
            ps.setInt(12,heightFront);
            ps.setInt(13,heightRear);
            ps.setInt(14,roofLength);
            ps.setInt(15,roofWidth);
            ps.setInt(16,exteriorWidthAtPost);
            ps.setInt(17, parkingLength);
            ps.setInt(18, parkingWidth);
            ps.setInt(19, shedDepth);
            ps.setInt(20, shedWidth);
            ps.setInt(21, SpecificationID);
            int rowsAffected = ps.executeUpdate();

        if ( rowsAffected != 1 )
        {
            throw new DatabaseException("Failed to update specification with ID: " + SpecificationID);
        }
        }
        catch (DatabaseException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static void deleteSpecification(int SpecificationID, ConnectionPool connectionPool) throws SQLException
    {
        String sql = "DELETE FROM specifications WHERE specification_id = ?";

        try(Connection connection = connectionPool.getConnection();
        PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setInt(1,SpecificationID);
            int rowsAffected = ps.executeUpdate();

            if ( rowsAffected != 1 )
            {
                throw new DatabaseException("Failed to delet user with ID: " + SpecificationID);
            }
        }
        catch (DatabaseException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static Specification createAndGetSpecification(long EAN, String model, int roomFor, boolean shed, int post,
                                                          int beam, int rafter, int roof, int fasciaBoard, int length, int width, int heightFront,
                                                          int heightRear, int roofLength, int roofWidth, int exteriorWidthAtPost, int parkingLength,
                                                          int parkingWidth, int shedDepth, int shedWidth, ConnectionPool connectionPool) throws SQLException, DatabaseException
    {
        String sql = "INSERT INTO specifications (EAN,model,room_for,shed,post,beam,rafter, roof, " +
                "fascia_board, length, width, height_front, height_rear, roof_length, roof_width," +
                " exterior_width_at_post, parking_length, parking_width,  shed_depth, shed_width)" +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS))
        {
            ps.setLong(1, EAN);
            ps.setString(2, model);
            ps.setInt(3, roomFor);
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
            ResultSet rs = ps.getGeneratedKeys();

            if (rs.next())
            {
                return getSpecificationByID(rs.getInt(1), connectionPool);
            }
            else
            {
                throw new DatabaseException("No ID was generated for the new specification");
            }
        }
    }

    public static void updateSpecification(int requestId, int width, int length, boolean shed, int shedWidth, int shedLength, int roof, ConnectionPool connectionPool) throws DatabaseException, SQLException
    {
      String sql = "UPDATE specifications SET length = ?, width = ?, shed = ?, shed_width = ?, shed_depth = ?, roof = ? " +
              "FROM carports JOIN carport_requests USING (carport_id) WHERE carports.specifications = specifications.specification_id " +
              "AND carport_requests.carport_request_id = ?";

      try(Connection connection = connectionPool.getConnection();
      PreparedStatement ps = connection.prepareStatement(sql))
      {
          ps.setInt(1, length);
          ps.setInt(2, width);
          ps.setBoolean(3, shed);
          ps.setInt(4, shedWidth);
          ps.setInt(5, shedLength);
          ps.setInt(6, roof);
          ps.setInt(7, requestId);
          int rowsAffected = ps.executeUpdate();

          if ( rowsAffected != 1 )
          {
              throw new DatabaseException("Failed to update specifications with ID: " + requestId);
          }
      }
}
}
