package com.university.app.ui;

import com.university.app.dao.DatabaseDAO;
import com.university.app.dao.TableViewerDAO;
import com.university.app.dao.GenericDAO;
// import com.university.app.dao.UserDAO;
// import com.university.app.model.User;
// import com.university.app.service.UserSession;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.sql.SQLException;

public class DataExplorerFrame extends JFrame {
    private JList<String> tableList;
    private JPanel mainContentPanel;
    private TableViewerDAO tableViewerDAO;
    private GenericDAO genericDAO;
    private String currentTable;
    private JTable currentJTable;
    private JButton addButton, updateButton, deleteButton;

    public DataExplorerFrame() {
        setTitle("Data Explorer");
        setSize(1024, 768);
        setLocationRelativeTo(null); // Center on screen
        setLayout(new BorderLayout(10, 10));

        tableViewerDAO = new TableViewerDAO();
        genericDAO = new GenericDAO();

        // Sidebar with table list
        DefaultListModel<String> listModel = new DefaultListModel<>();
        populateTableList(listModel);
        
        tableList = new JList<>(listModel);
        tableList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                displayTableData(tableList.getSelectedValue());
            }
        });

        JScrollPane sidebarScrollPane = new JScrollPane(tableList);
        sidebarScrollPane.setPreferredSize(new Dimension(200, 0));
        add(sidebarScrollPane, BorderLayout.WEST);

        // Main content panel (for tables)
        mainContentPanel = new JPanel(new BorderLayout());
        
        JPanel crudPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        addButton = new JButton("Add");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");
        crudPanel.add(addButton);
        crudPanel.add(updateButton);
        crudPanel.add(deleteButton);
        mainContentPanel.add(crudPanel, BorderLayout.NORTH);

        addButton.addActionListener(e -> openDynamicDialog(null));
        updateButton.addActionListener(e -> openDynamicDialog(getSelectedRowData()));
        deleteButton.addActionListener(e -> deleteSelectedRecord());

        add(mainContentPanel, BorderLayout.CENTER);
        
        if (listModel.getSize() > 0) {
            tableList.setSelectedIndex(0);
        }
    }

    private void displayTableData(String tableName) {
        if (tableName == null) return;
        this.currentTable = tableName;

        Component[] components = mainContentPanel.getComponents();
        for (Component component : components) {
            if (component instanceof JScrollPane) {
                mainContentPanel.remove(component);
            }
        }

        JTable table = new JTable(tableViewerDAO.getTableModelFor(tableName));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        this.currentJTable = table; // Keep a reference to the current table
        mainContentPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
        updateCrudButtonState();
    }
    
    private void updateCrudButtonState() {
        // In the admin's data explorer, all buttons are always visible.
        // For entry users, this logic is handled within MainFrame.
        addButton.setVisible(true);
        updateButton.setVisible(true);
        deleteButton.setVisible(true);
    }

    private void openDynamicDialog(Map<String, Object> initialData) {
        if (currentTable == null) return;
        DynamicRecordDialog dialog = new DynamicRecordDialog(this, currentTable, initialData);
        dialog.setVisible(true);
        displayTableData(currentTable); // Refresh table after dialog closes
    }

    private Map<String, Object> getSelectedRowData() {
        if (currentJTable == null || currentJTable.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        Map<String, Object> rowData = new HashMap<>();
        int selectedRow = currentJTable.getSelectedRow();
        for (int i = 0; i < currentJTable.getColumnCount(); i++) {
            String colName = currentJTable.getColumnName(i);
            Object colValue = currentJTable.getValueAt(selectedRow, i);
            rowData.put(colName, colValue);
        }
        return rowData;
    }

    private void deleteSelectedRecord() {
        Map<String, Object> rowData = getSelectedRowData();
        if (rowData == null) {
            // getSelectedRowData already showed a message
            return;
        }

        int response = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this record?", "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (response != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            Map<String, Object> whereClauses = new HashMap<>();
            List<String> primaryKeys = genericDAO.getPrimaryKeys(currentTable);
            if (primaryKeys.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Cannot delete record: No primary key defined for this table.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            for (String pk : primaryKeys) {
                whereClauses.put(pk, rowData.get(pk));
            }

            genericDAO.deleteRecord(currentTable, whereClauses);
            JOptionPane.showMessageDialog(this, "Record deleted successfully!");
            displayTableData(currentTable); // Refresh
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error deleting record: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void populateTableList(DefaultListModel<String> listModel) {
        DatabaseDAO databaseDAO = new DatabaseDAO();
        List<String> tables = databaseDAO.getAllTableNames();
        tables.forEach(listModel::addElement);
    }
} 