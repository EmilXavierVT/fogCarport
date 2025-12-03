package app.persistence;

import app.entities.Product;
import app.entities.ProductInOrder;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TestMapper {

    public static int count(String table, ConnectionPool connectionPool) throws SQLException {
        String sql = "SELECT COUNT(*) AS number_of_rows FROM " + table;

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("number_of_rows");

            } else {
                throw new DatabaseException("No data found");
            }
        } catch (SQLException | DatabaseException e) {
            throw new RuntimeException();
        }
    }
}