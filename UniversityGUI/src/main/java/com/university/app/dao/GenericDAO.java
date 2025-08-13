package com.university.app.dao;

import com.university.app.db.DatabaseConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenericDAO {

    public List<String> getColumnNames(String tableName) throws SQLException {
        List<String> columnNames = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet rs = metaData.getColumns(conn.getCatalog(), null, tableName, null);
            while (rs.next()) {
                columnNames.add(rs.getString("COLUMN_NAME"));
            }
        }
        return columnNames;
    }

    public List<String> getPrimaryKeys(String tableName) throws SQLException {
        List<String> primaryKeys = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet rs = metaData.getPrimaryKeys(conn.getCatalog(), null, tableName);
            while (rs.next()) {
                primaryKeys.add(rs.getString("COLUMN_NAME"));
            }
        }
        return primaryKeys;
    }

    public void insertRecord(String tableName, Map<String, Object> values) throws SQLException {
        StringBuilder sql = new StringBuilder("INSERT INTO ").append(tableName).append(" (");
        StringBuilder placeholders = new StringBuilder();
        List<String> columns = new ArrayList<>();

        for (Map.Entry<String, Object> entry : values.entrySet()) {
            columns.add(entry.getKey());
            if (placeholders.length() > 0) {
                placeholders.append(", ");
            }
            placeholders.append("?");
        }

        sql.append(String.join(", ", columns)).append(") VALUES (").append(placeholders).append(")");

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            int i = 1;
            for (String column : columns) {
                pstmt.setObject(i++, values.get(column));
            }
            pstmt.executeUpdate();
        }
    }

    public void updateRecord(String tableName, Map<String, Object> values, Map<String, Object> whereClauses) throws SQLException {
        StringBuilder sql = new StringBuilder("UPDATE ").append(tableName).append(" SET ");
        List<String> setColumns = new ArrayList<>();
        for (String column : values.keySet()) {
            setColumns.add(column + " = ?");
        }
        sql.append(String.join(", ", setColumns));

        List<String> whereColumns = new ArrayList<>();
        if (whereClauses != null && !whereClauses.isEmpty()) {
            sql.append(" WHERE ");
            for (String column : whereClauses.keySet()) {
                whereColumns.add(column + " = ?");
            }
            sql.append(String.join(" AND ", whereColumns));
        }

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            int i = 1;
            for (Object value : values.values()) {
                pstmt.setObject(i++, value);
            }
            if (whereClauses != null) {
                for (Object value : whereClauses.values()) {
                    pstmt.setObject(i++, value);
                }
            }
            pstmt.executeUpdate();
        }
    }

    public void deleteRecord(String tableName, Map<String, Object> whereClauses) throws SQLException {
        StringBuilder sql = new StringBuilder("DELETE FROM ").append(tableName).append(" WHERE ");
        List<String> whereColumns = new ArrayList<>();
        for (String column : whereClauses.keySet()) {
            whereColumns.add(column + " = ?");
        }
        sql.append(String.join(" AND ", whereColumns));

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            int i = 1;
            for (Object value : whereClauses.values()) {
                pstmt.setObject(i++, value);
            }
            pstmt.executeUpdate();
        }
    }

    // Returns a map: column name -> referenced table/column (as "table.column")
    public Map<String, String> getForeignKeys(String tableName) throws SQLException {
        Map<String, String> foreignKeys = new HashMap<>();
        try (Connection conn = DatabaseConnector.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet rs = metaData.getImportedKeys(conn.getCatalog(), null, tableName);
            while (rs.next()) {
                String fkColumn = rs.getString("FKCOLUMN_NAME");
                String pkTable = rs.getString("PKTABLE_NAME");
                String pkColumn = rs.getString("PKCOLUMN_NAME");
                foreignKeys.put(fkColumn, pkTable + "." + pkColumn);
            }
        }
        return foreignKeys;
    }

    // Returns all values for a referenced column (for dropdowns)
    public List<String> getReferencedColumnValues(String refTable, String refColumn) throws SQLException {
        List<String> values = new ArrayList<>();
        String sql = "SELECT DISTINCT " + refColumn + " FROM " + refTable;
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                values.add(rs.getString(1));
            }
        }
        return values;
    }

    // Returns a map: column name -> SQL type (java.sql.Types int)
    public Map<String, Integer> getColumnTypes(String tableName) throws SQLException {
        Map<String, Integer> columnTypes = new HashMap<>();
        try (Connection conn = DatabaseConnector.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet rs = metaData.getColumns(conn.getCatalog(), null, tableName, null);
            while (rs.next()) {
                String colName = rs.getString("COLUMN_NAME");
                int dataType = rs.getInt("DATA_TYPE");
                columnTypes.put(colName, dataType);
            }
        }
        return columnTypes;
    }

    // Returns a list of (ID, label) pairs for a referenced table/column
    public List<String[]> getReferenceIdLabelPairs(String refTable, String refColumn) throws SQLException {
        List<String[]> pairs = new ArrayList<>();
        // Heuristic: look for a label column
        String labelCol = null;
        try (Connection conn = DatabaseConnector.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet rs = metaData.getColumns(conn.getCatalog(), null, refTable, null);
            while (rs.next()) {
                String col = rs.getString("COLUMN_NAME").toLowerCase();
                if (!col.equals(refColumn.toLowerCase()) && (col.contains("name") || col.contains("title") || col.contains("desc"))) {
                    labelCol = rs.getString("COLUMN_NAME");
                    break;
                }
            }
        }
        String sql = "SELECT DISTINCT " + refColumn + (labelCol != null ? ", " + labelCol : "") + " FROM " + refTable;
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String id = rs.getString(1);
                String label = (labelCol != null) ? rs.getString(2) : id;
                pairs.add(new String[]{id, label});
            }
        }
        return pairs;
    }
} 