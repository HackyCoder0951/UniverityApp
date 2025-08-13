package com.university.app.ui;

import javax.swing.*;
import java.awt.*;
import com.university.app.dao.RoleDAO;
import com.university.app.model.Role;
import java.sql.SQLException;
import java.util.List;

public class RoleManagementPanel extends JPanel {
    private DefaultListModel<Role> roleListModel;
    private JList<Role> roleList;
    private JButton addButton, editButton, deleteButton;

    public RoleManagementPanel() {
        setLayout(new BorderLayout(10, 10));
        roleListModel = new DefaultListModel<>();
        roleList = new JList<>(roleListModel);
        JScrollPane scrollPane = new JScrollPane(roleList);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        addButton = new JButton("Add Role");
        editButton = new JButton("Edit Role");
        deleteButton = new JButton("Delete Role");
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);

        loadRoles();
        addButton.addActionListener(e -> addRoleDialog());
        editButton.addActionListener(e -> editRoleDialog());
        deleteButton.addActionListener(e -> deleteRoleDialog());
    }

    private void loadRoles() {
        roleListModel.clear();
        List<Role> roles = new RoleDAO().getAllRoles();
        for (Role r : roles) roleListModel.addElement(r);
    }

    private void addRoleDialog() {
        String name = JOptionPane.showInputDialog(this, "Enter new role name:");
        if (name != null && !name.trim().isEmpty()) {
            try {
                new RoleDAO().addRole(name.trim());
                loadRoles();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error adding role: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editRoleDialog() {
        Role selected = roleList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select a role to edit.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String name = JOptionPane.showInputDialog(this, "Edit role name:", selected.getName());
        if (name != null && !name.trim().isEmpty()) {
            try {
                new RoleDAO().updateRole(selected.getId(), name.trim());
                loadRoles();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error updating role: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteRoleDialog() {
        Role selected = roleList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select a role to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete role '" + selected.getName() + "'?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                new RoleDAO().deleteRole(selected.getId());
                loadRoles();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting role: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
} 