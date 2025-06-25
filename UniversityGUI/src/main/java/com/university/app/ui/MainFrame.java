package com.university.app.ui;

import com.university.app.App;
import com.university.app.dao.DatabaseDAO;
import com.university.app.dao.GenericDAO;
import com.university.app.dao.PasswordRequestDAO;
import com.university.app.dao.TableViewerDAO;
import com.university.app.dao.UserDAO;
import com.university.app.model.User;
import com.university.app.service.UserSession;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The main window of the application, displayed after a successful login.
 * The content and capabilities of this frame change based on the logged-in user's role.
 * - Admin users see a dashboard with administrative panels.
 * - Entry/Reporting users see a data viewer/editor for their permitted tables.
 */
public class MainFrame extends JFrame {
    private final UserSession session = UserSession.getInstance();
    private final User currentUser = session.getCurrentUser();

    // UI components for data viewing roles
    private JList<String> tableList;
    private JTable dataTable;
    private JScrollPane tableScrollPane;

    // DAOs
    private final DatabaseDAO databaseDAO = new DatabaseDAO();
    private final TableViewerDAO tableViewerDAO = new TableViewerDAO();
    private final GenericDAO genericDAO = new GenericDAO();
    private PasswordRequestDAO passwordRequestDAO = new PasswordRequestDAO();

    // CRUD buttons
    private JButton addButton, updateButton, deleteButton;

    // --- State for User UI ---
    private String currentTable;
    private JTable currentJTable;
    private JPanel userMainContentPanel;
    private JButton userAddButton, userUpdateButton, userDeleteButton;
    // --- End State for User UI ---

    /**
     * Constructs the main frame. It initializes the UI based on the user's role.
     */
    public MainFrame() {
        setTitle("University ERP - " + currentUser.getRole() + " (" + currentUser.getUsername() + ")");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Build the UI based on user role
        if ("admin".equals(currentUser.getRole())) {
            createAdminUI();
        } else {
            createDataUserUI();
        }
    }

    /**
     * Creates the UI for 'admin' users.
     * This UI consists of a tabbed pane with User Management and Password Request panels.
     */
    private void createAdminUI() {
        JTabbedPane tabbedPane = new JTabbedPane();

        // User Management Panel
        tabbedPane.addTab("User Management", new UserManagementPanel());

        // Password Requests Panel
        tabbedPane.addTab("Password Requests", new PasswordRequestPanel());

        // A panel for main actions like Logout and Data Explorer
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton dataExplorerButton = new JButton("Data Explorer");
        dataExplorerButton.addActionListener(e -> new DataExplorerFrame().setVisible(true));
        topPanel.add(dataExplorerButton);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> logout());
        topPanel.add(logoutButton);

        // Add components to the frame
        add(topPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }

    /**
     * Creates the UI for non-admin users ('entry', 'reporting').
     * This UI consists of a list of permitted tables and a data viewer.
     */
    private void createDataUserUI() {
        // Main content split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(200);

        // Left panel: List of tables the user is permitted to see
        List<String> permittedTables = session.getUserPermissions();
        DefaultListModel<String> listModel = new DefaultListModel<>();
        if (permittedTables != null) {
            for(String table : permittedTables) {
                listModel.addElement(table);
            }
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
        // Reporting users cannot edit data directly in the table, entry can.
        dataTable.setEnabled("entry".equals(currentUser.getRole()));
        tableScrollPane = new JScrollPane(dataTable);
        splitPane.setRightComponent(tableScrollPane);

        add(splitPane, BorderLayout.CENTER);

        // Bottom panel for buttons (Logout, CRUD)
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        // CRUD buttons are only visible to 'entry' role users
        if ("entry".equals(currentUser.getRole())) {
            addButton = new JButton("Add");
            addButton.addActionListener(e -> addRecord());
            bottomPanel.add(addButton);

            updateButton = new JButton("Update");
            updateButton.addActionListener(e -> updateRecord());
            bottomPanel.add(updateButton);

            deleteButton = new JButton("Delete");
            deleteButton.addActionListener(e -> deleteRecord());
            bottomPanel.add(deleteButton);
        }
        
        // All non-admin users can request a password change
        JButton changePasswordButton = new JButton("Request Password Change");
        changePasswordButton.addActionListener(e -> requestPasswordChange());
        bottomPanel.add(changePasswordButton);


        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> logout());
        bottomPanel.add(logoutButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Handles the process of a user requesting a password change.
     */
    private void requestPasswordChange() {
        int response = JOptionPane.showConfirmDialog(this,
                "This will send a password change request to an administrator. Are you sure?",
                "Confirm Password Change Request",
                JOptionPane.YES_NO_OPTION);

        if (response == JOptionPane.YES_OPTION) {
            try {
                new PasswordRequestDAO().createRequest(currentUser.getId());
                JOptionPane.showMessageDialog(this,
                        "Your request has been sent to the administrator.",
                        "Request Sent",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "There was an error sending your request: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Logs the current user out, clears the session, and returns to the login screen.
     */
    private void logout() {
        App.showLogin();
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

    // --- CRUD Methods for 'entry' role ---

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

        // Convert the selected JTable row to a map of data for the dialog
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

            // Find the column index of the primary key
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