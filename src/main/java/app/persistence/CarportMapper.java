package app.persistence;

import app.entities.Carport;
import app.entities.StandardCarport;
import app.entities.UserDefinedCarport;
import app.exceptions.DatabaseException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CarportMapper
{
    public static void SaveCarportInDB(String name, double price, int type, String productionDescription, int specification, ConnectionPool connectionPool) throws DatabaseException
    {
        String sql = "INSERT INTO carports (name, price, type, product_description, specifications) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setString(1, name);
            ps.setDouble(2, (price));
            ps.setInt(3, type);
            ps.setString(4, productionDescription);
            ps.setInt(5, specification);
            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new DatabaseException("Error in creating a new carport", e.getMessage());
        }
    }

    public static void SaveCarportInDB(String name, double price, int type, String productionDescription, int specification, String pdf, ConnectionPool connectionPool) throws DatabaseException
    {
        String sql = "INSERT INTO carports (name, price, type, product_description, specifications, pdf_file) VALUES (?, ?, ?, ?, ? ,?)";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setString(1, name);
            ps.setDouble(2, price);
            ps.setInt(3, type);
            ps.setString(4, productionDescription);
            ps.setInt(5, specification);
            ps.setString(6, pdf);
            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new DatabaseException("Error in creating a new carport", e.getMessage());
        }
    }

    public static int SaveAndGetCarportInDB(String name, double price, int type, String productionDescription, int specification, ConnectionPool connectionPool) throws DatabaseException
    {
        String sql = "INSERT INTO carports (name, price, type, product_description, specifications) VALUES (?, ?, ?, ?, ?) RETURNING carport_id";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setString(1, name);
            ps.setDouble(2, (price));
            ps.setInt(3, type);
            ps.setString(4, productionDescription);
            ps.setInt(5, specification);
            ps.executeQuery();

            ResultSet rs = ps.getResultSet();
            rs.next();
            return rs.getInt("carport_id");
        }
        catch (SQLException e)
        {
            throw new DatabaseException("Error in creating a new carport", e.getMessage());
        }

    }
    

    public static Carport getCarportByID(int carportID,ConnectionPool connectionPool) throws DatabaseException
    {
        String sql = "SELECT * FROM carports WHERE carport_id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setInt(1, carportID);
            ResultSet rs = ps.executeQuery();

            if (rs.next())
            {
                if (rs.getString("pdf_file") != null)
                {
                    return new StandardCarport(
                            rs.getInt("carport_id"),
                            rs.getString("name"),
                            rs.getFloat("price"),
                            rs.getInt("type"),
                            rs.getString("product_description"),
                            SpecificationMapper.getSpecificationByID(rs.getInt("specifications"),connectionPool),
                            rs.getString("pdf_file"));
                }
                if (rs.getString("pdf_file") == null)
                {
                   return new UserDefinedCarport(rs.getInt("carport_id"),
                            rs.getString("name"),
                            rs.getFloat("price"),
                            rs.getInt("type"),
                            rs.getString("product_description"),
                            SpecificationMapper.getSpecificationByID(rs.getInt("specifications"),connectionPool));
                }
            }
        }
        catch (SQLException e)
        {
            throw new DatabaseException("Error: no carport found", e.getMessage());
        }
        throw new DatabaseException("No carport found " + carportID);
    }

    public static void deleteCarport(int carportID,ConnectionPool connectionPool) throws DatabaseException
    {
        String sql = "DELETE FROM carports WHERE carport_id = ?";

        try(Connection connection = connectionPool.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setInt(1, carportID);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected != 1)
            {
                throw new DatabaseException("Error deleting carport " + carportID);
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static void updateCarport(int carportID, String name, int price, int type, String product_description, int specifications, ConnectionPool connectionPool)
    {
        String sql = "UPDATE carports SET name=?, price=?, type=?, product_description=?, specifications=? WHERE carport_id=?";

        try(Connection connection = connectionPool.getConnection();
        PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setString(1, name);
            ps.setInt(2, price);
            ps.setInt(3, type);
            ps.setString(4, product_description);
            ps.setInt(5, specifications);

            ps.setInt(6, carportID);
            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static void updateCarport(int carportID, String name, int price, int type, String product_description, int specifications, String pdfFile, ConnectionPool connectionPool)
    {
        String sql = "UPDATE carports SET name=?, price=?, type=?, product_description=?, specifications=?, pdf_file=? WHERE carport_id=?";

        try(Connection connection = connectionPool.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setString(1, name);
            ps.setInt(2, price);
            ps.setInt(3, type);
            ps.setString(4, product_description);
            ps.setInt(5, specifications);
            ps.setString(6, pdfFile);
            ps.setInt(7, carportID);
            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static List<Carport> getAllStandardCarport(ConnectionPool connectionPool) throws DatabaseException
    {
        ArrayList<Carport> carports = new ArrayList<>();
        String sql = "SELECT * FROM carports ORDER BY type ASC";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ResultSet rs = ps.executeQuery();

            while (rs.next())
            {
                if (rs.getString("pdf_file") != null)
                {
                    Carport carport = new StandardCarport(
                            rs.getInt("carport_id"),
                            rs.getString("name"),
                            rs.getFloat("price"),
                            rs.getInt("type"),
                            rs.getString("product_description"),
                            SpecificationMapper.getSpecificationByID(rs.getInt("specifications"),connectionPool),
                            rs.getString("pdf_file"));
                    carports.add(carport);
                }
            }
                return carports;

        }
        catch (SQLException e)
        {
            throw new DatabaseException("Error: no carport found", e.getMessage());
        }
    }
    public static List<Carport> getAllStandardCarportForSlider(ConnectionPool connectionPool) throws DatabaseException
    {
        ArrayList<Carport> carports = new ArrayList<>();
        String sql = "SELECT * FROM carports ORDER BY type ASC";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ResultSet rs = ps.executeQuery();

            while (rs.next())
            {
                if (rs.getString("pdf_file") != null)
                {
                    Carport carport = new StandardCarport(
                            rs.getInt("carport_id"),
                            rs.getString("name"),
                            rs.getFloat("price"),
                            rs.getInt("type"),
                            rs.getString("product_description"),
                            rs.getString("pdf_file"));
                    carports.add(carport);
                }
            }
                return carports;

        }
        catch (SQLException e)
        {
            throw new DatabaseException("Error: no carport found", e.getMessage());
        }
    }


    public static void changeTypeToDeletedByID(int carportID, ConnectionPool connectionPool) throws SQLException {
        String sql = "UPDATE carports SET type = ? WHERE carport_id = ?";

        try(Connection connection = connectionPool.getConnection();
        PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setInt(1, 404);
            ps.setInt(2, carportID);
            int rowsAffected = ps.executeUpdate();

            if ( rowsAffected != 1 )
            {
                throw new DatabaseException("Failed to move carprot to removed by ID: " + carportID);
            }

        } catch (DatabaseException e) {
            throw new RuntimeException(e);
        }
    }
}









