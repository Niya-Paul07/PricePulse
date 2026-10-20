
package com.pricepulse.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ProductInsertTest {

    public static void main(String[] args) {

        String sql = "INSERT INTO products (product_name, category, unit) "
                   + "VALUES (?, ?, ?)";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, "Test Apple");
            statement.setString(2, "Fruits");
            statement.setString(3, "kg");

            int rows = statement.executeUpdate();

            System.out.println(rows + " product inserted successfully!");

        } catch (SQLException e) {
            System.out.println("Insert failed!");
            System.out.println(e.getMessage());
        }
    }
}
