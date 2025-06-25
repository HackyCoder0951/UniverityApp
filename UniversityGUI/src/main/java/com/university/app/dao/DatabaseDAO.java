package com.university.app.dao;

import com.university.app.db.DatabaseConnector;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Data Access Object for retrieving database-level metadata.
 * This class is specifically used to get a list of all tables in the database,
 * which is used to populate the data exploration UI.
 */
public class DatabaseDAO {

    /**
     * Fetches the names of all tables in the 'UniversityDB' schema.
     * It filters out system tables and returns only user-defined tables.
     *
     * @return A list of table names as strings.
     */
    public List<String> getAllTableNames() {
        List<String> tableNames = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            // Fetches tables from the current schema/catalog for "TABLE" type.
            ResultSet rs = metaData.getTables(conn.getCatalog(), null, "%", new String[]{"TABLE"});
            while (rs.next()) {
                tableNames.add(rs.getString("TABLE_NAME"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tableNames.stream()
                .filter(name -> !name.equalsIgnoreCase("users"))
                .collect(Collectors.toList());
    }
} 