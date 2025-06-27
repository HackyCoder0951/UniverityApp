package com.university.app.ui;

import com.university.app.dao.UserDAO;
import com.university.app.dao.RoleDAO;
import com.university.app.model.User;
import com.university.app.model.Role;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;

public class UpdateUserDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<Role> roleComboBox;
    private JTextField uidField;
    private UserDAO userDAO;
    private User userToUpdate;

    public UpdateUserDialog(Frame owner, User userToUpdate) {
        super(owner, "Update User", true);
        this.userDAO = new UserDAO();
        this.userToUpdate = userToUpdate;
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setToolTipText("Edit the user's login name");
        gbc.gridx = 0; gbc.gridy = 0;
        add(usernameLabel, gbc);
        usernameField = new JTextField(userToUpdate.getUsername(), 16);
        usernameField.setToolTipText("Edit the user's login name");
        gbc.gridx = 1; gbc.gridy = 0;
        add(usernameField, gbc);

        JLabel passwordLabel = new JLabel("Password (leave blank to keep current):");
        passwordLabel.setToolTipText("Leave blank to keep the current password");
        gbc.gridx = 0; gbc.gridy = 1;
        add(passwordLabel, gbc);
        passwordField = new JPasswordField(16);
        passwordField.setToolTipText("Leave blank to keep the current password");
        gbc.gridx = 1; gbc.gridy = 1;
        add(passwordField, gbc);

        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setToolTipText("Select the user's role");
        gbc.gridx = 0; gbc.gridy = 2;
        add(roleLabel, gbc);
        roleComboBox = new JComboBox<>();
        java.util.List<Role> roles = new RoleDAO().getAllRoles();
        for (Role role : roles) {
            roleComboBox.addItem(role);
            if (role.getName().equalsIgnoreCase(userToUpdate.getRole())) {
                roleComboBox.setSelectedItem(role);
            }
        }
        roleComboBox.setToolTipText("Select the user's role");
        gbc.gridx = 1; gbc.gridy = 2;
        add(roleComboBox, gbc);

        JLabel uidLabel = new JLabel("UID:");
        uidLabel.setToolTipText("User ID (not editable)");
        gbc.gridx = 0; gbc.gridy = 3;
        add(uidLabel, gbc);
        uidField = new JTextField(userToUpdate.getUid(), 16);
        uidField.setEditable(false);
        uidField.setBackground(new Color(235, 235, 235));
        uidField.setToolTipText("User ID (not editable)");
        gbc.gridx = 1; gbc.gridy = 3;
        add(uidField, gbc);

        JButton saveButton = new JButton("Save");
        saveButton.setToolTipText("Save changes to the user");
        saveButton.addActionListener(e -> saveUser());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(saveButton);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(buttonPanel, gbc);

        setResizable(false);
        pack();
        setLocationRelativeTo(owner);
    }

    private void saveUser() {
        String newUsername = usernameField.getText();
        String newPassword = new String(passwordField.getPassword());
        Role selectedRole = (Role) roleComboBox.getSelectedItem();
        String uid = uidField.getText();

        if (newUsername.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String passwordToSave = newPassword.isEmpty() ? userToUpdate.getPassword() : newPassword;
        User updatedUser = new User(uid, newUsername, passwordToSave, String.valueOf(selectedRole.getId()), userToUpdate.isRequiresPasswordReset());

        try {
            userDAO.updateUser(userToUpdate.getUsername(), updatedUser);
            JOptionPane.showMessageDialog(this, "User updated successfully!");
            dispose();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating user: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
} 