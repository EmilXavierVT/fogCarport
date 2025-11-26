package app.persistence;

import app.entities.User;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserMapper
{
    public static User createUser(String firstName, String lastName, int zipCode, String streetname, int houseNumber, String floor, String email, String password, ConnectionPool connectionPool) throws DatabaseException
    {
        String sql = "INSERT INTO users (first_name,last_name,zip_code,street_name,house_number,floor,email, password) " +
                "VALUES (?, ? , ? , ? , ? , ? , ? , ? ) RETURNING user_id";

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
                return getUserByID(rs.getInt(1), connectionPool);
            } else
            {
                throw new DatabaseException("Failed to create new user");
            }
        } catch (SQLException e)
        {
            throw new DatabaseException("Error creating user", e.getMessage());
        }
    }
    public static User getUserByID(int id,ConnectionPool connectionPool) throws DatabaseException
    {
        String sql = "SELECT * FROM users WHERE user_id = ?";

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
                                  String streetName, Integer houseNumber, String floor ,ConnectionPool connectionPool) throws DatabaseException
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
            return getUserByID(id,connectionPool);
        } catch (SQLException e)
        {
            throw new DatabaseException("Error updating user", e.getMessage());
        }
    }

    public static void deleteUser(int id,ConnectionPool connectionPool) throws DatabaseException
    {
        String sql = "DELETE FROM users WHERE id=?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected != 1)
            {
                throw new DatabaseException("Failed to delete user with ID: " + id);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error deleting user", e.getMessage());
        }
    }

    public static User login(String email, String password, ConnectionPool connectionPool) throws DatabaseException
    {
        String sql = "select user_id from users where email=? and password=?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)
        )
        {
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if ( rs.next() )
            {
                int id = rs.getInt("user_id");

                return getUserByID(id,connectionPool);
            } else
            {
                throw new DatabaseException("Fejl i login. Prøv igen");
            }
        }
        catch (SQLException e)
        {
            throw new DatabaseException("DB fejl", e.getMessage());
        }
    }

    public static int checkIfAdmin(User user,ConnectionPool connectionPool) throws DatabaseException
    {
        String sql = "SELECT role FROM users WHERE user_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)
        ){
            ps.setInt(1, user.getUserId());
            ResultSet rs = ps.executeQuery();

            if(rs.next())
            {
                int role = rs.getInt("role");
                return role;
            }
        } catch (SQLException e)
        {
            throw new DatabaseException("something admin login", e.getMessage());
        }
        return 0;
    }

    public static List<User> getAllUsers(ConnectionPool connectionPool) throws DatabaseException
    {
        List<User> userList = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ResultSet rs = ps.executeQuery();

            while (rs.next())
            {
                userList.add(new User(
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
                ));
            }
            return userList;
        } catch (SQLException e)
        {
            throw new DatabaseException("Error retrieving all users", e.getMessage());
        }
    }
}


