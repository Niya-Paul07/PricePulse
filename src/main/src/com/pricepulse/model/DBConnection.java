
package com.pricepulse.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/pricepulse";

    private static final String USER = "root";

    public static Connection getConnection() throws SQLException {
        String password = System.getenv("PRICEPULSE_DB_PASSWORD");

        if (password == null || password.isBlank()) {
            throw new SQLException(
                "Please set the PRICEPULSE_DB_PASSWORD environment variable."
            );
        }

        return DriverManager.getConnection(URL, USER, password);
    }
}
