package com.university.app.dao;

import com.university.app.db.DatabaseConnector;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DatabaseDAO {

    public List<String> getAllTableNames() {
        List<String> tableNames = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            String[] types = {"TABLE"};
            ResultSet rs = metaData.getTables(conn.getCatalog(), null, "%", types);
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