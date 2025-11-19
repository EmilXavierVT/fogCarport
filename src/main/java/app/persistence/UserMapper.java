package app.persistence;

import app.enteties.User;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper {

    public static User createUser(String firstName, String lastName, int zipCode, String streetname, int houseNumber, String floor, String email, String password) throws DatabaseException
    {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        String sql = "INSERT INTO users (first_name,last_name,zip_code,street_name,house_number,floor,email, password) " +
                "VALUES (?, ? , ? , ? , ? , ? , ? , ? ) RETURNING id";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setInt(3, zipCode);
            ps.setString(4, streetname);
            ps.setInt(5, houseNumber);
            ps.setString(6, floor);
            ps.setString(7, email);
            ps.setString(8, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next())
            {
                return getUser(rs.getInt(1));
            } else
            {
                throw new DatabaseException("Failed to create new user");
            }
        } catch (SQLException e)
        {
            throw new DatabaseException("Error creating user", e.getMessage());
        }
    }
    public static User getUser(int id) throws DatabaseException
    {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if ( rs.next() )
            {
                return new User(
                        rs.getInt("user_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getInt("zip_code"),
                        rs.getString("street_name"),
                        rs.getInt("house_number"),
                        rs.getString("floor"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getInt("role")

                );
            } else
            {
                throw new DatabaseException("No user found with ID: " + id);
            }
        } catch (SQLException e)
        {
            throw new DatabaseException("Error retrieving user", e.getMessage());
        }
    }

    public static User updateUser(int id, String firstName, String lastName, int zipCode,
                                  String streetName, Integer houseNumber, String floor,
                                  ConnectionPool connectionPool) throws DatabaseException
    {
        String sql = "UPDATE users SET first_name=?, last_name=?, zip_code=?, street_name=?, " +
                "house_number=?, floor=? WHERE id=?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setInt(3, zipCode);
            ps.setString(4, streetName);
            ps.setInt(5, houseNumber);
            ps.setString(6, floor);

            ps.setInt(7, id);

            int rowsAffected = ps.executeUpdate();
            if ( rowsAffected != 1 )
            {
                throw new DatabaseException("Failed to update user with ID: " + id);
            }
            return getUser(id);
        } catch (SQLException e)
        {
            throw new DatabaseException("Error updating user", e.getMessage());
        }
    }

    public static void deleteUser(int id) throws DatabaseException
    {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        String sql = "DELETE FROM users WHERE id=?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected != 1) {
                throw new DatabaseException("Failed to delete user with ID: " + id);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error deleting user", e.getMessage());
        }
    }
}


