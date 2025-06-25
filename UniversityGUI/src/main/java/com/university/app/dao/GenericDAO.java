package com.university.app.dao;

import com.university.app.db.DatabaseConnector;

import java.sql.*;
import java.util.ArrayList;
//import java.util.HashMap;
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
} 