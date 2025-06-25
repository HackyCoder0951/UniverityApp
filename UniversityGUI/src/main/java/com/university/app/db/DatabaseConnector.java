package com.university.app.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * A utility class for managing the connection to the UniversityDB database.
 * This class provides a centralized point for obtaining a database connection,
 * making it easy to manage connection details like URL, user, and password.
 */
public class DatabaseConnector {

    // Database connection details.
    // In a production environment, these should be stored securely in a configuration file.
    private static final String URL = "jdbc:mysql://localhost:3306/UniversityDB";
    private static final String USER = "root"; //  default user
    private static final String PASSWORD = "Vitc@123"; // default password

    /**
     * Establishes and returns a connection to the database.
     *
     * @return A Connection object to the database.
     * @throws SQLException if a database access error occurs.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
} 