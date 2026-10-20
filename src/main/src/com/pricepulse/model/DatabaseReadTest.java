
package com.pricepulse.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

public class DatabaseReadTest {

    public static void main(String[] args) {

        String query = "SHOW TABLES";

        try (
            Connection connection = DBConnection.getConnection();
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(query)
        ) {
            System.out.println("Tables in PricePulse database:");

            while (result.next()) {
                System.out.println("- " + result.getString(1));
            }

        } catch (SQLException e) {
            System.out.println("Could not read database tables.");
            System.out.println(e.getMessage());
        }
    }
}
