package com.university.app.ui;

import com.university.app.dao.UserDAO;
import com.university.app.service.UserSession;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;

/**
 * A dialog that forces a user to reset their password.
 * This dialog appears after a user logs in if their 'requires_password_reset' flag is true.
 * The user cannot proceed with the application until they have successfully set a new password.
 */
public class ForcePasswordResetDialog extends JDialog {
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private boolean passwordReset = false;

    /**
     * Constructs the force password reset dialog.
     * @param owner The parent dialog (typically the LoginDialog).
     */
    public ForcePasswordResetDialog(JDialog owner) {
        super(owner, "Create New Password", true);

        // Prevent user from closing the window without resetting the password
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                JOptionPane.showMessageDialog(owner,
                        "You must reset your password to continue.",
                        "Action Required",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        // --- Layout and Components ---
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints cs = new GridBagConstraints();
        cs.fill = GridBagConstraints.HORIZONTAL;
        cs.insets = new Insets(5, 5, 5, 5);

        panel.add(new JLabel("New Password:"), cs);
        newPasswordField = new JPasswordField(20);
        cs.gridy = 1;
        panel.add(newPasswordField, cs);

        cs.gridy = 2;
        panel.add(new JLabel("Confirm New Password:"), cs);
        confirmPasswordField = new JPasswordField(20);
        cs.gridy = 3;
        panel.add(confirmPasswordField, cs);

        JButton saveButton = new JButton("Save Password");
        saveButton.addActionListener(e -> resetPassword());
        cs.gridy = 4;
        panel.add(saveButton, cs);

        add(panel);
        pack();
        setLocationRelativeTo(owner);
    }

    /**
     * Handles the logic for resetting the user's password.
     */
    private void resetPassword() {
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (newPassword.isEmpty() || !newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match or are empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        UserDAO userDAO = new UserDAO();
        int userId = UserSession.getInstance().getCurrentUser().getId();

        try {
            // Update the password in the database
            userDAO.updatePassword(userId, newPassword);

            // Set the 'requires_password_reset' flag to false
            userDAO.setRequiresPasswordReset(userId, false);

            JOptionPane.showMessageDialog(this, "Password has been successfully reset. Please log in again.");
            this.passwordReset = true; // Signal success
            dispose(); // Close the dialog
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error resetting password: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    /**
     * Checks if the password was successfully reset by the user.
     * @return true if the password was reset, false otherwise.
     */
    public boolean isPasswordReset() {
        return passwordReset;
    }
} 