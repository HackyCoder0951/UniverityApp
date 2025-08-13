package com.university.app.ui;

import com.university.app.dao.DatabaseDAO;
import com.university.app.dao.UserDAO;
import com.university.app.model.User;
import com.university.app.service.UserSession;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class UserManagementPanel extends JPanel {
    private JList<User> userList;
    private DefaultListModel<User> userListModel;
    private JList<String> permissionList;
    private DefaultListModel<String> permissionListModel;
    private UserDAO userDAO;

    public UserManagementPanel() {
        super(new BorderLayout(10, 10));
        userDAO = new UserDAO();

        // User List Panel
        JPanel userPanel = new JPanel(new BorderLayout());
        userPanel.setBorder(BorderFactory.createTitledBorder("Users"));
        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userList.setCellRenderer(new UserListCellRenderer());
        userList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updatePermissionList(userList.getSelectedValue());
            }
        });
        userPanel.add(new JScrollPane(userList), BorderLayout.CENTER);

        JPanel userActionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton addUserButton = new JButton("Add User");
        JButton updateUserButton = new JButton("Update User");
        JButton deleteUserButton = new JButton("Delete User");
        userActionsPanel.add(addUserButton);
        userActionsPanel.add(updateUserButton);
        userActionsPanel.add(deleteUserButton);
        userPanel.add(userActionsPanel, BorderLayout.SOUTH);

        addUserButton.addActionListener(e -> openAddUserDialog());
        updateUserButton.addActionListener(e -> openUpdateUserDialog());
        deleteUserButton.addActionListener(e -> deleteSelectedUser());

        // Permission List Panel
        JPanel permissionPanel = new JPanel(new BorderLayout());
        permissionPanel.setBorder(BorderFactory.createTitledBorder("Table Permissions"));
        permissionListModel = new DefaultListModel<>();
        permissionList = new JList<>(permissionListModel);
        
        DatabaseDAO databaseDAO = new DatabaseDAO();
        List<String> allTables = databaseDAO.getAllTableNames();
        permissionList.setCellRenderer(new PermissionListCellRenderer(allTables));
        permissionPanel.add(new JScrollPane(permissionList), BorderLayout.CENTER);
        
        JPanel saveButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton savePermissionsButton = new JButton("Save Permissions");
        savePermissionsButton.addActionListener(e -> savePermissions());
        saveButtonPanel.add(savePermissionsButton);
        permissionPanel.add(saveButtonPanel, BorderLayout.SOUTH);

        // Add panels to main layout
        add(userPanel, BorderLayout.WEST);
        add(permissionPanel, BorderLayout.CENTER);

        loadUsers();
    }

    private void loadUsers() {
        userListModel.clear();
        String currentUsername = UserSession.getInstance().getCurrentUser().getUsername();
        List<User> users = userDAO.getAllUsers(currentUsername);
        users.forEach(userListModel::addElement);
    }
    
    private void updatePermissionList(User user) {
        permissionListModel.clear();
        permissionList.clearSelection();
        if (user == null) {
            permissionList.setEnabled(false);
            return;
        }

        boolean isSelectableRole = "entry".equals(user.getRole()) || "reporting".equals(user.getRole());
        permissionList.setEnabled(isSelectableRole);

        if (!isSelectableRole) {
            return;
        }
        
        DatabaseDAO databaseDAO = new DatabaseDAO();
        List<String> allTables = databaseDAO.getAllTableNames();
        allTables.forEach(permissionListModel::addElement);
        
        List<String> grantedPermissions = userDAO.getPermissionsForUser(user.getUsername());
        for (String permission : grantedPermissions) {
            int index = allTables.indexOf(permission);
            if (index != -1) {
                permissionList.addSelectionInterval(index, index);
            }
        }
    }

    private void savePermissions() {
        User selectedUser = userList.getSelectedValue();
        if (selectedUser == null) {
            JOptionPane.showMessageDialog(this, "Please select a user.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        boolean isSelectableRole = "entry".equals(selectedUser.getRole()) || "reporting".equals(selectedUser.getRole());
        if (!isSelectableRole) {
            JOptionPane.showMessageDialog(this, "Permissions can only be set for 'entry' or 'reporting' users.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // First, remove all old permissions
            List<String> oldPermissions = userDAO.getPermissionsForUser(selectedUser.getUsername());
            for(String table : oldPermissions) {
                userDAO.removePermission(selectedUser.getUsername(), table);
            }

            // Then, add all newly selected permissions
            List<String> newPermissions = permissionList.getSelectedValuesList();
            for (String table : newPermissions) {
                userDAO.addPermission(selectedUser.getUsername(), table);
            }
            JOptionPane.showMessageDialog(this, "Permissions updated successfully!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error saving permissions: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void openAddUserDialog() {
        AddUserDialog addUserDialog = new AddUserDialog((Frame) SwingUtilities.getWindowAncestor(this));
        addUserDialog.setVisible(true);
        loadUsers(); // Refresh user list after dialog closes
    }

    private void openUpdateUserDialog() {
        User selectedUser = userList.getSelectedValue();
        if (selectedUser == null) {
            JOptionPane.showMessageDialog(this, "Please select a user to update.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        UpdateUserDialog updateUserDialog = new UpdateUserDialog((Frame) SwingUtilities.getWindowAncestor(this), selectedUser);
        updateUserDialog.setVisible(true);
        loadUsers(); // Refresh user list
    }

    private void deleteSelectedUser() {
        User selectedUser = userList.getSelectedValue();
        if (selectedUser == null) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int response = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete the user '" + selectedUser.getUsername() + "'?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (response == JOptionPane.YES_OPTION) {
            try {
                userDAO.deleteUser(selectedUser.getUsername());
                JOptionPane.showMessageDialog(this, "User deleted successfully!");
                loadUsers(); // Refresh user list
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting user: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    // Custom cell renderer to display user's name and role
    private static class UserListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof User) {
                User user = (User) value;
                setText(String.format("<html>%s <i style='color:gray;'>(%s)</i></html>", user.getUsername(), user.getRole()));
            }
            return this;
        }
    }
    
    // Custom cell renderer to show checkboxes for permissions
    private static class PermissionListCellRenderer extends JCheckBox implements ListCellRenderer<String> {
        public PermissionListCellRenderer(List<String> allTables) {
            // This renderer is stateful based on selection, which is not ideal, but works for this case.
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends String> list, String value, int index, boolean isSelected, boolean cellHasFocus) {
            setText(value);
            setSelected(isSelected);
            setEnabled(list.isEnabled());
            setFont(list.getFont());
            setBackground(list.getBackground());
            setForeground(list.getForeground());
            return this;
        }
    }
} 