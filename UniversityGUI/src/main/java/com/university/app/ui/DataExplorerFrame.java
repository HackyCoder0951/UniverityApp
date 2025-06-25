package com.university.app.ui;

import com.university.app.dao.DatabaseDAO;
import com.university.app.dao.GenericDAO;
import com.university.app.dao.TableViewerDAO;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A dedicated window for admin users to explore and manage data in any table.
 * It features a list of all database tables and a viewer to display the data,
 * along with full CRUD (Create, Read, Update, Delete) capabilities.
 */
public class DataExplorerFrame extends JFrame {
    private JList<String> tableList;
    private JTable dataTable;
    private final DatabaseDAO databaseDAO = new DatabaseDAO();
    private final TableViewerDAO tableViewerDAO = new TableViewerDAO();
    private final GenericDAO genericDAO = new GenericDAO();

    /**
     * Constructs the Data Explorer window.
     */
    public DataExplorerFrame() {
        setTitle("Data Explorer");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Close only this window
        setLocationRelativeTo(null);

        // Main layout with a split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(200);

        // Left panel: List of all database tables
        List<String> allTables = databaseDAO.getAllTableNames();
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for(String table : allTables) {
            listModel.addElement(table);
        }
        tableList = new JList<>(listModel);
        tableList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                viewTable(tableList.getSelectedValue());
            }
        });
        splitPane.setLeftComponent(new JScrollPane(tableList));

        // Right panel: Table data viewer
        dataTable = new JTable();
        dataTable.setFillsViewportHeight(true);
        JScrollPane tableScrollPane = new JScrollPane(dataTable);
        splitPane.setRightComponent(tableScrollPane);
        add(splitPane, BorderLayout.CENTER);

        // Bottom panel for CRUD buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> addRecord());
        bottomPanel.add(addButton);

        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(e -> updateRecord());
        bottomPanel.add(updateButton);

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteRecord());
        bottomPanel.add(deleteButton);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Displays the data for the selected table in the main data table.
     * @param tableName The name of the table to display.
     */
    private void viewTable(String tableName) {
        if (tableName == null) return;
        TableModel model = tableViewerDAO.getTableData(tableName);
        dataTable.setModel(model);
    }

    // --- CRUD Methods ---

    /**
     * Opens a dynamic dialog to add a new record to the currently selected table.
     */
    private void addRecord() {
        String selectedTable = tableList.getSelectedValue();
        if (selectedTable == null) {
            JOptionPane.showMessageDialog(this, "Please select a table first.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        DynamicRecordDialog dialog = new DynamicRecordDialog(this, selectedTable, null);
        dialog.setVisible(true);
        viewTable(selectedTable); // Refresh table after dialog closes
    }

    /**
     * Opens a dynamic dialog to update the selected record in the current table.
     */
    private void updateRecord() {
        String selectedTable = tableList.getSelectedValue();
        int selectedRow = dataTable.getSelectedRow();

        if (selectedTable == null || selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a record to update.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Map<String, Object> recordData = new HashMap<>();
        for (int i = 0; i < dataTable.getColumnCount(); i++) {
            recordData.put(dataTable.getColumnName(i), dataTable.getValueAt(selectedRow, i));
        }

        DynamicRecordDialog dialog = new DynamicRecordDialog(this, selectedTable, recordData);
        dialog.setVisible(true);
        viewTable(selectedTable); // Refresh table
    }

    /**
     * Deletes the selected record from the current table.
     */
    private void deleteRecord() {
        String selectedTable = tableList.getSelectedValue();
        int selectedRow = dataTable.getSelectedRow();

        if (selectedTable == null || selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a record to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int response = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this record?", "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (response != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            // Get table metadata to find the primary key
            Map<String, Object> metadata = genericDAO.getTableMetadata(selectedTable);
            String pkColumnName = (String) metadata.get("primaryKey");

            if (pkColumnName == null || pkColumnName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Cannot delete: Table does not have a primary key defined.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Find the column index of the primary key in the JTable
            int pkColumnIndex = -1;
            for (int i = 0; i < dataTable.getColumnCount(); i++) {
                if (pkColumnName.equalsIgnoreCase(dataTable.getColumnName(i))) {
                    pkColumnIndex = i;
                    break;
                }
            }

            if (pkColumnIndex == -1) {
                JOptionPane.showMessageDialog(this, "Cannot delete: Primary key column not found in the table view.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Get the value of the primary key from the selected row
            Object pkValue = dataTable.getValueAt(selectedRow, pkColumnIndex);

            // Call the generic DAO to delete the record
            genericDAO.deleteRecord(selectedTable, pkColumnName, pkValue);

            JOptionPane.showMessageDialog(this, "Record deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            viewTable(selectedTable); // Refresh the table
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error deleting record: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
