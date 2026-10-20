
package com.pricepulse.model;

import java.sql.Connection;
import java.sql.SQLException;

public class DBConnectionTest {

    public static void main(String[] args) {
        try (Connection connection = DBConnection.getConnection()) {
            System.out.println("SUCCESS! Java connected to MySQL!");
        } catch (SQLException e) {
            System.out.println("Connection failed!");
            System.out.println(e.getMessage());
        }
    }
}
