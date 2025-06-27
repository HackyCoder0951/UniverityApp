package com.university.app.ui;

import com.university.app.App;
// import com.university.app.dao.DatabaseDAO;
import com.university.app.dao.GenericDAO;
import com.university.app.dao.PasswordRequestDAO;
import com.university.app.dao.TableViewerDAO;
import com.university.app.dao.UserDAO;
import com.university.app.model.User;
import com.university.app.service.UserSession;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainFrame extends JFrame {

    // --- State for User UI ---
    private String currentTable;
    private JTable currentJTable;
    private GenericDAO genericDAO = new GenericDAO();
    private TableViewerDAO tableViewerDAO = new TableViewerDAO();
    private JPanel userMainContentPanel;
    private JButton userAddButton, userUpdateButton, userDeleteButton;
    // --- End State for User UI ---

    public MainFrame() {
        User currentUser = UserSession.getInstance().getCurrentUser();
        if (currentUser != null && "admin".equals(currentUser.getRole())) {
            buildAdminUI();
        } else {
            buildUserUI();
        }
    }

    private void buildAdminUI() {
        setTitle("University ERP - Admin Dashboard");
        setSize(1024, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create JTabbedPane for admin
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("User Management", new UserManagementPanel());
        tabbedPane.addTab("Password Requests", new PasswordRequestPanel());
        tabbedPane.addTab("Data Explorer", new DataExplorerPanel());

        // Load background image
        java.net.URL imgUrl = getClass().getClassLoader().getResource("images/ysFxGz.jpg");
        ImageIcon bgIcon = new ImageIcon(imgUrl);
        BackgroundPanel backgroundPanel = new BackgroundPanel(bgIcon.getImage());
        backgroundPanel.setLayout(new BorderLayout());
        backgroundPanel.add(tabbedPane, BorderLayout.CENTER);

        // Bottom button panel
        JPanel bottomButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton dataExplorerButton = new JButton("Data Explorer (Window)");
        JButton roleManagementButton = new JButton("Role Management");
        // JButton marksEntryButton = new JButton("Marks Entry");
        // JButton resultsViewButton = new JButton("Results View");
        JButton changePasswordButton = new JButton("Change Password");
        JButton logoutButton = new JButton("Logout");
        bottomButtonPanel.add(dataExplorerButton);
        bottomButtonPanel.add(roleManagementButton);
        // bottomButtonPanel.add(marksEntryButton);
        // bottomButtonPanel.add(resultsViewButton);
        bottomButtonPanel.add(changePasswordButton);
        bottomButtonPanel.add(logoutButton);
        backgroundPanel.add(bottomButtonPanel, BorderLayout.SOUTH);

        dataExplorerButton.addActionListener(e -> new DataExplorerFrame().setVisible(true));
        roleManagementButton.addActionListener(e -> new RoleManagementDialog(this).setVisible(true));
        // marksEntryButton.addActionListener(e -> new MarksEntryDialog(this).setVisible(true));
        // resultsViewButton.addActionListener(e -> new ResultViewDialog(this).setVisible(true));
        changePasswordButton.addActionListener(e -> new ChangePasswordDialog(this).setVisible(true));
        logoutButton.addActionListener(e -> App.showLogin());

        setContentPane(backgroundPanel);
    }

    private void buildUserUI() {
        setTitle("University ERP");
        setSize(1024, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create JTabbedPane for entry/reporting users
        JTabbedPane tabbedPane = new JTabbedPane();

        // Data Entry Tab (existing sidebar/table view)
        JPanel dataEntryPanel = new JPanel(new BorderLayout(10, 10));
        this.tableViewerDAO = new TableViewerDAO();
        DefaultListModel<String> listModel = new DefaultListModel<>();
        UserDAO userDAO = new UserDAO();
        userDAO.getPermissionsForUser(UserSession.getInstance().getCurrentUser().getUsername()).forEach(listModel::addElement);
        JList<String> tableList = new JList<>(listModel);
        tableList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane sidebarScrollPane = new JScrollPane(tableList);
        sidebarScrollPane.setPreferredSize(new Dimension(200, 0));
        dataEntryPanel.add(sidebarScrollPane, BorderLayout.WEST);
        userMainContentPanel = new JPanel(new BorderLayout());
        dataEntryPanel.add(userMainContentPanel, BorderLayout.CENTER);
        tableList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedTable = tableList.getSelectedValue();
                displayUserData(selectedTable);
            }
        });
        JPanel bottomButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        userAddButton = new JButton("Add");
        userUpdateButton = new JButton("Update");
        userDeleteButton = new JButton("Delete");
        JButton userRefreshButton = new JButton("Refresh");
        bottomButtonPanel.add(userAddButton);
        bottomButtonPanel.add(userUpdateButton);
        bottomButtonPanel.add(userDeleteButton);
        bottomButtonPanel.add(userRefreshButton);
        bottomButtonPanel.add(new JSeparator(SwingConstants.VERTICAL));
        // JButton marksEntryButton = new JButton("Marks Entry");
        // JButton resultsViewButton = new JButton("Results View");
        // bottomButtonPanel.add(marksEntryButton);
        // bottomButtonPanel.add(resultsViewButton);
        JButton changePasswordButton = new JButton("Request Password Change");
        JButton logoutButton = new JButton("Logout");
        bottomButtonPanel.add(changePasswordButton);
        bottomButtonPanel.add(logoutButton);
        dataEntryPanel.add(bottomButtonPanel, BorderLayout.SOUTH);
        userAddButton.addActionListener(e -> openDynamicDialog(null));
        userUpdateButton.addActionListener(e -> openDynamicDialog(getSelectedRowData()));
        userDeleteButton.addActionListener(e -> deleteSelectedRecord());
        userRefreshButton.addActionListener(e -> displayUserData(currentTable));
        // marksEntryButton.addActionListener(e -> new MarksEntryDialog(this).setVisible(true));
        // resultsViewButton.addActionListener(e -> new ResultViewDialog(this).setVisible(true));
        changePasswordButton.addActionListener(e -> {
            int response = JOptionPane.showConfirmDialog(this, "This will send a password change request to the administrator. Continue?", "Request Password Change", JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                try {
                    new PasswordRequestDAO().createRequest(UserSession.getInstance().getCurrentUser().getUsername());
                    JOptionPane.showMessageDialog(this, "Request sent successfully!");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error sending request: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        logoutButton.addActionListener(e -> App.showLogin());
        tabbedPane.addTab("Data Entry", dataEntryPanel);
        // Remove Marks Entry and Results View tabs for entry/reporting users
        // tabbedPane.addTab("Marks Entry", new MarksEntryPanel());
        // tabbedPane.addTab("Results View", new ResultViewPanel());
        add(tabbedPane, BorderLayout.CENTER);
    }

    private void displayUserData(String tableName) {
        if (tableName == null) return;
        this.currentTable = tableName;

        Component[] components = userMainContentPanel.getComponents();
        for (Component component : components) {
            if (component instanceof JScrollPane) {
                userMainContentPanel.remove(component);
            }
        }

        this.currentJTable = new JTable(tableViewerDAO.getTableModelFor(tableName));
        this.currentJTable.setDefaultEditor(Object.class, null); // Always read-only for non-admins
        userMainContentPanel.add(new JScrollPane(this.currentJTable), BorderLayout.CENTER);
        userMainContentPanel.revalidate();
        userMainContentPanel.repaint();

        UserDAO userDAO = new UserDAO();
        List<String> permissions = userDAO.getPermissionsForUser(UserSession.getInstance().getCurrentUser().getUsername());
        boolean hasPermission = permissions.contains(tableName);
        userAddButton.setVisible(hasPermission);
        userUpdateButton.setVisible(hasPermission);
        userDeleteButton.setVisible(hasPermission);
    }

    private void openDynamicDialog(Map<String, Object> initialData) {
        if (currentTable == null) return;

        if (initialData == null && currentJTable != null && currentJTable.getSelectedRow() != -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DynamicRecordDialog dialog = new DynamicRecordDialog(this, currentTable, initialData);
        dialog.setVisible(true);
        displayUserData(currentTable); 
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
            displayUserData(currentTable);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error deleting record: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
} 