package app.persistence;

import app.entities.Carport;
import app.entities.StandardCarport;
import app.entities.UserDefinedCarport;
import app.exceptions.DatabaseException;

import javax.xml.transform.Result;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CarportMapper {

    public static void putCarportInDB(String name, int price, int type, String productionDescription, int specification) throws DatabaseException {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        String sql = "INSERT INTO carport (name, price, type, production_description, specification) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setInt(2, price);
            ps.setInt(3, type);
            ps.setString(4, productionDescription);
            ps.setInt(5, specification);
            ps.executeQuery();

        } catch (SQLException e) {
            throw new DatabaseException("Error in creating a new carport", e.getMessage());
        }
    }

    public static Carport CarportByID(int carportID) throws DatabaseException {
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        String sql = "SELECT * FROM carport WHERE id = ?";

        try (Connection conection = connectionPool.getConnection();
             PreparedStatement ps = conection.prepareStatement(sql)) {
            ps.setInt(1, carportID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                if (rs.getString("pdf_file") != null) {
                    return new StandardCarport(
                            rs.getInt("carport_id"),
                            rs.getString("name"),
                            rs.getFloat("price"),
                            rs.getInt("type"),
                            rs.getString("production_description"),
                            rs.getInt("specification"),
                            rs.getString("pdf_file"));

                }
                if (rs.getString("pdf_file") == null)
                {
                   return new UserDefinedCarport(rs.getInt("carport_id"),
                            rs.getString("name"),
                            rs.getFloat("price"),
                            rs.getInt("type"),
                            rs.getString("production_description"),
                            rs.getInt("specification"));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error: no carport found", e.getMessage());
        }
        throw new DatabaseException("No carport found " + carportID);
    }
}








