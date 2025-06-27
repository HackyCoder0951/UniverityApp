package com.university.app.ui;

import com.university.app.dao.UserDAO;
import com.university.app.model.User;
import com.university.app.service.UserSession;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class ChangePasswordDialog extends JDialog {
    private JPasswordField oldPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;

    public ChangePasswordDialog(Frame owner) {
        super(owner, "Change Password", true);
        setLayout(new GridLayout(4, 2));

        add(new JLabel("Old Password:"));
        oldPasswordField = new JPasswordField();
        add(oldPasswordField);

        add(new JLabel("New Password:"));
        newPasswordField = new JPasswordField();
        add(newPasswordField);

        add(new JLabel("Confirm New Password:"));
        confirmPasswordField = new JPasswordField();
        add(confirmPasswordField);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> changePassword());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(saveButton);
        add(buttonPanel);

        pack();
        setLocationRelativeTo(owner);
    }

    private void changePassword() {
        User currentUser = UserSession.getInstance().getCurrentUser();
        String oldPassword = new String(oldPasswordField.getPassword());
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (!currentUser.getPassword().equals(oldPassword)) {
            JOptionPane.showMessageDialog(this, "Old password does not match.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (newPassword.isEmpty() || !newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "New passwords do not match or are empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            UserDAO userDAO = new UserDAO();
            userDAO.updatePassword(currentUser.getUsername(), newPassword);
            
            // Update password in current session as well
            currentUser = new User(currentUser.getUid(), currentUser.getUsername(), newPassword, currentUser.getRole(), false);
            UserSession.getInstance().setCurrentUser(currentUser);

            JOptionPane.showMessageDialog(this, "Password updated successfully!");
            dispose();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating password: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
} 