package com.university.app.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {

    // In a real application, these should be in a properties file
    private static final String URL = "jdbc:mysql://localhost:3306/UniversityDB";
    private static final String USER = "root"; //  default user
    private static final String PASSWORD = "vitc@123"; // default password

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
} 