package app.persistence;

import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StandardCarportMapper {

    public static void createStandardCarport(String name, int price, int type, String description, int specification, String pdf_file, ConnectionPool connectionPool) throws DatabaseException
    {
        String sql = "INSERT INTO standard_carports (name, price, type, product_description, specificatons, pdf_file) " +
                "VALUES (?, ? , ? , ? , ? , ? )";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setString(1, name);
            ps.setInt(1, price);
            ps.setInt(1, type);
            ps.setString(1, description);
            ps.setInt(1, specification);
            ps.setString(1, pdf_file);
            ps.executeQuery();

        } catch (SQLException e)
        {
            throw new DatabaseException("Error creating user", e.getMessage());
        }
    }

    public static void updateStandardCarport(int id, String name, int price, int type, String description, int specification, String pdf_file, ConnectionPool connectionPool) throws DatabaseException
    {
        String sql = "UPDATE standard_carports SET name=?, price=?, type=?, product_description=?, specificatons=?, pdf_file=? WHERE standard_id=?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setString(1, name);
            ps.setInt(1, price);
            ps.setInt(1, type);
            ps.setString(1, description);
            ps.setInt(1, specification);
            ps.setString(1, pdf_file);
            ps.setInt(7, id);
            int rowsAffected = ps.executeUpdate();

            if ( rowsAffected != 1 )
            {
                throw new DatabaseException("Failed to update user with ID: " + id);
            }
        } catch (SQLException e)
        {
            throw new DatabaseException("Error updating user", e.getMessage());
        }
    }

}
