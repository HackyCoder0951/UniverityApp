package com.university.app.dao;

import com.university.app.db.DatabaseConnector;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.sql.*;
import java.util.Vector;

/**
 * Data Access Object for fetching data to display in a JTable.
 * This class can retrieve all data from a specified table and format it
 * into a TableModel, which can be directly used by a JTable component.
 */
public class TableViewerDAO {

    /**
     * Fetches all data from a given table and converts it into a TableModel.
     * The method first retrieves the table's metadata to get column names,
     * then fetches all rows of data.
     *
     * @param tableName The name of the table to retrieve data from.
     * @return A TableModel containing the data and column headers of the specified table.
     */
    public TableModel getTableData(String tableName) {
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

            DefaultTableModel model = new DefaultTableModel(data, columnNames);
            return model;

        } catch (SQLException e) {
            e.printStackTrace();
            // Return an empty model in case of error
            return new DefaultTableModel();
        }
    }
} 