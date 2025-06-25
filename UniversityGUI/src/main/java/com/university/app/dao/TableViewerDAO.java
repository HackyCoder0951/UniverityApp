package com.university.app.dao;

import com.university.app.db.DatabaseConnector;

import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.Vector;

public class TableViewerDAO {

    public DefaultTableModel getTableModelFor(String tableName) {
        String sql = "SELECT * FROM " + tableName;
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            ResultSetMetaData metaData = rs.getMetaData();

            // Column Names
            Vector<String> columnNames = new Vector<>();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                columnNames.add(metaData.getColumnName(i));
            }

            // Data of the table
            Vector<Vector<Object>> data = new Vector<>();
            while (rs.next()) {
                Vector<Object> vector = new Vector<>();
                for (int i = 1; i <= columnCount; i++) {
                    vector.add(rs.getObject(i));
                }
                data.add(vector);
            }

            return new DefaultTableModel(data, columnNames);

        } catch (SQLException e) {
            e.printStackTrace();
            // Return an empty model in case of error
            return new DefaultTableModel();
        }
    }
} 