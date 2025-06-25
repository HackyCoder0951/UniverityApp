package com.university.app.ui;

import com.university.app.dao.UserDAO;
import com.university.app.model.User;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class UpdateUserDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    private UserDAO userDAO;
    private User userToUpdate;

    public UpdateUserDialog(Frame owner, User userToUpdate) {
        super(owner, "Update User", true);
        this.userDAO = new UserDAO();
        this.userToUpdate = userToUpdate;

        setLayout(new GridLayout(4, 2));

        add(new JLabel("Username:"));
        usernameField = new JTextField(userToUpdate.getUsername());
        add(usernameField);

        add(new JLabel("Password (leave blank to keep current):"));
        passwordField = new JPasswordField();
        add(passwordField);

        add(new JLabel("Role:"));
        roleComboBox = new JComboBox<>(new String[]{"entry", "reporting"});
        roleComboBox.setSelectedItem(userToUpdate.getRole());
        add(roleComboBox);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> saveUser());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(saveButton);
        add(buttonPanel);

        pack();
        setLocationRelativeTo(owner);
    }

    private void saveUser() {
        String newUsername = usernameField.getText();
        String newPassword = new String(passwordField.getPassword());
        String newRole = (String) roleComboBox.getSelectedItem();

        if (newUsername.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String passwordToSave = newPassword.isEmpty() ? userToUpdate.getPassword() : newPassword;
        User updatedUser = new User(newUsername, passwordToSave, newRole, userToUpdate.isRequiresPasswordReset());

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