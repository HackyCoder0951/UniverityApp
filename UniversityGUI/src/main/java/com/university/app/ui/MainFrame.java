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
        System.out.println("DEBUG: Current user: " + (currentUser != null ? currentUser.getUsername() : "null") +
                           ", role: " + (currentUser != null ? currentUser.getRole() : "null"));
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
        JButton loginHistoryButton = new JButton("Login History");
        bottomButtonPanel.add(dataExplorerButton);
        bottomButtonPanel.add(roleManagementButton);
        // bottomButtonPanel.add(marksEntryButton);
        // bottomButtonPanel.add(resultsViewButton);
        bottomButtonPanel.add(changePasswordButton);
        bottomButtonPanel.add(logoutButton);
        bottomButtonPanel.add(loginHistoryButton);
        backgroundPanel.add(bottomButtonPanel, BorderLayout.SOUTH);

        dataExplorerButton.addActionListener(e -> new DataExplorerFrame().setVisible(true));
        roleManagementButton.addActionListener(e -> new RoleManagementDialog(this).setVisible(true));
        // marksEntryButton.addActionListener(e -> new MarksEntryDialog(this).setVisible(true));
        // resultsViewButton.addActionListener(e -> new ResultViewDialog(this).setVisible(true));
        changePasswordButton.addActionListener(e -> new ChangePasswordDialog(this).setVisible(true));
        logoutButton.addActionListener(e -> {
            User currentUser = UserSession.getInstance().getCurrentUser();
            if (currentUser != null) {
                try {
                    new com.university.app.dao.LoginHistoryDAO().logLogout(currentUser.getUid());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            App.showLogin();
        });
        loginHistoryButton.addActionListener(e -> showLoginHistoryDialog());

        setContentPane(backgroundPanel);
    }

    private void buildUserUI() {
        setTitle("University ERP");
        setSize(1024, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        User currentUser = UserSession.getInstance().getCurrentUser();
        boolean isEntry = currentUser != null && "entry".equals(currentUser.getRole());
        // boolean isReporting = currentUser != null && "reporting".equals(currentUser.getRole());

        // Create JTabbedPane for entry/reporting users
        JTabbedPane tabbedPane = new JTabbedPane();

        // Data Entry Tab (existing sidebar/table view)
        JPanel dataEntryPanel = new JPanel(new BorderLayout(10, 10));
        this.tableViewerDAO = new TableViewerDAO();
        DefaultListModel<String> listModel = new DefaultListModel<>();
        UserDAO userDAO = new UserDAO();
        userDAO.getPermissionsForUser(currentUser.getUsername()).forEach(listModel::addElement);
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
        if (isEntry) {
            bottomButtonPanel.add(userAddButton);
            bottomButtonPanel.add(userUpdateButton);
            bottomButtonPanel.add(userDeleteButton);
        }
        bottomButtonPanel.add(userRefreshButton);
        bottomButtonPanel.add(new JSeparator(SwingConstants.VERTICAL));
        if (isEntry) {
            JButton marksEntryButton = new JButton("Marks Entry");
            JButton resultsViewButton = new JButton("Results View");
            bottomButtonPanel.add(marksEntryButton);
            bottomButtonPanel.add(resultsViewButton);
            marksEntryButton.addActionListener(e -> new MarksEntryDialog(this).setVisible(true));
            resultsViewButton.addActionListener(e -> new ResultViewDialog(this).setVisible(true));
        }
        JButton changePasswordButton = new JButton("Request Password Change");
        JButton logoutButton = new JButton("Logout");
        bottomButtonPanel.add(changePasswordButton);
        bottomButtonPanel.add(logoutButton);
        dataEntryPanel.add(bottomButtonPanel, BorderLayout.SOUTH);
        userAddButton.addActionListener(e -> openDynamicDialog(null));
        userUpdateButton.addActionListener(e -> openDynamicDialog(getSelectedRowData()));
        userDeleteButton.addActionListener(e -> deleteSelectedRecord());
        userRefreshButton.addActionListener(e -> displayUserData(currentTable));
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
        logoutButton.addActionListener(e -> {
            User cu = UserSession.getInstance().getCurrentUser();
            if (cu != null) {
                try {
                    new com.university.app.dao.LoginHistoryDAO().logLogout(cu.getUid());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            App.showLogin();
        });
        tabbedPane.addTab("Data Entry", dataEntryPanel);
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

    private void showLoginHistoryDialog() {
        java.util.List<com.university.app.model.LoginHistory> history = new com.university.app.dao.LoginHistoryDAO().getAllHistory();
        String[] columns = {"UID", "Username", "Login Time", "Logout Time", "Active"};
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        for (com.university.app.model.LoginHistory lh : history) {
            model.addRow(new Object[]{
                lh.getUid(), lh.getUsername(), lh.getLoginTime(), lh.getLogoutTime(), lh.isIsActive() ? "Yes" : "No"
            });
        }
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        JOptionPane.showMessageDialog(this, scrollPane, "Login History", JOptionPane.INFORMATION_MESSAGE);
    }

    // private String mapRole(Object dbRole) {
    //     if (dbRole == null) return null;
    //     String roleStr = dbRole.toString();
    //     switch (roleStr) {
    //         case "1": return "admin";
    //         case "2": return "entry";
    //         case "3": return "reporting";
    //         default: return roleStr;
    //     }
    // }
} 