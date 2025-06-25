package com.university.app.dao;

import com.university.app.db.DatabaseConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A generic Data Access Object for performing dynamic CRUD (Create, Read, Update, Delete) operations.
 * This class uses database metadata to work with any table, constructing SQL queries at runtime.
 * It is the backbone of the dynamic data exploration and editing features of the application.
 */
public class GenericDAO {

    /**
     * Fetches the metadata for a given table, such as column names and primary key.
     *
     * @param tableName The name of the table to get metadata for.
     * @return A Map containing two keys: "columns" (a List of column names) and "primaryKey" (the name of the primary key column).
     * @throws SQLException if a database access error occurs.
     */
    public Map<String, Object> getTableMetadata(String tableName) throws SQLException {
        Map<String, Object> metadata = new HashMap<>();

        try (Connection conn = DatabaseConnector.getConnection()) {
            DatabaseMetaData dbMetaData = conn.getMetaData();

            // Get column names
            List<String> columnNames = new ArrayList<>();
            try (ResultSet columnsRs = dbMetaData.getColumns(conn.getCatalog(), null, tableName, null)) {
                while (columnsRs.next()) {
                    columnNames.add(columnsRs.getString("COLUMN_NAME"));
                }
            }
            metadata.put("columns", columnNames);

            // Get primary key
            String primaryKey = "";
            try (ResultSet pkRs = dbMetaData.getPrimaryKeys(null, null, tableName)) {
                if (pkRs.next()) {
                    primaryKey = pkRs.getString("COLUMN_NAME");
                }
            }
            metadata.put("primaryKey", primaryKey);
        }
        return metadata;
    }

    /**
     * Inserts a new record into a specified table.
     *
     * @param tableName The name of the table to insert into.
     * @param recordData A Map where keys are column names and values are the data to be inserted.
     * @throws SQLException if the insert operation fails.
     */
    public void insertRecord(String tableName, Map<String, Object> recordData) throws SQLException {
        StringBuilder columns = new StringBuilder();
        StringBuilder placeholders = new StringBuilder();
        List<Object> values = new ArrayList<>();

        for (Map.Entry<String, Object> entry : recordData.entrySet()) {
            columns.append(entry.getKey()).append(",");
            placeholders.append("?,");
            values.add(entry.getValue());
        }

        columns.setLength(columns.length() - 1); // Remove last comma
        placeholders.setLength(placeholders.length() - 1); // Remove last comma

        String sql = "INSERT INTO " + tableName + " (" + columns + ") VALUES (" + placeholders + ")";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < values.size(); i++) {
                pstmt.setObject(i + 1, values.get(i));
            }

            pstmt.executeUpdate();
        }
    }

    /**
     * Updates an existing record in a specified table.
     *
     * @param tableName The name of the table to update.
     * @param recordData A Map of column names and their new values for the record.
     * @param primaryKeyColumnName The name of the primary key column.
     * @param primaryKeyValue The value of the primary key for the record to be updated.
     * @throws SQLException if the update operation fails.
     */
    public void updateRecord(String tableName, Map<String, Object> recordData, String primaryKeyColumnName, Object primaryKeyValue) throws SQLException {
        StringBuilder setClause = new StringBuilder();
        List<Object> values = new ArrayList<>();

        for (Map.Entry<String, Object> entry : recordData.entrySet()) {
            setClause.append(entry.getKey()).append(" = ?,");
            values.add(entry.getValue());
        }
        setClause.setLength(setClause.length() - 1); // Remove last comma
        values.add(primaryKeyValue); // Add PK value for the WHERE clause

        String sql = "UPDATE " + tableName + " SET " + setClause + " WHERE " + primaryKeyColumnName + " = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < values.size(); i++) {
                pstmt.setObject(i + 1, values.get(i));
            }

            pstmt.executeUpdate();
        }
    }

    /**
     * Deletes a record from a specified table based on its primary key.
     *
     * @param tableName The name of the table to delete from.
     * @param primaryKeyColumnName The name of the primary key column.
     * @param primaryKeyValue The value of the primary key for the record to be deleted.
     * @throws SQLException if the delete operation fails.
     */
    public void deleteRecord(String tableName, String primaryKeyColumnName, Object primaryKeyValue) throws SQLException {
        String sql = "DELETE FROM " + tableName + " WHERE " + primaryKeyColumnName + " = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setObject(1, primaryKeyValue);
            pstmt.executeUpdate();
        }
    }
} 