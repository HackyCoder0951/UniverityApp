package com.university.app.ui;

import com.university.app.dao.UserDAO;
import com.university.app.service.UserSession;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class ForcePasswordResetDialog extends JDialog {
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;

    public ForcePasswordResetDialog(Frame owner) {
        super(owner, "Create New Password", true);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); // User must reset
        setLayout(new GridLayout(3, 2));

        add(new JLabel("New Password:"));
        newPasswordField = new JPasswordField();
        add(newPasswordField);

        add(new JLabel("Confirm New Password:"));
        confirmPasswordField = new JPasswordField();
        add(confirmPasswordField);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> resetPassword());
        add(saveButton);

        pack();
        setLocationRelativeTo(owner);
    }

    private void resetPassword() {
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (newPassword.isEmpty() || !newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match or are empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            UserDAO userDAO = new UserDAO();
            String username = UserSession.getInstance().getCurrentUser().getUsername();
            
            // Update the password
            userDAO.updatePassword(username, newPassword);
            
            // Set the reset flag to false
            userDAO.setRequiresPasswordResetFlag(username, false);

            JOptionPane.showMessageDialog(this, "Password has been successfully reset!");
            dispose();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error resetting password: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
} 