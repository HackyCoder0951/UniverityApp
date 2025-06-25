package com.university.app.ui;

import com.university.app.dao.UserDAO;
import com.university.app.model.User;
import com.university.app.service.UserSession;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * A dialog window for user authentication.
 * This is the entry point for the application's UI. It collects username and
 * password, validates them against the database, and on success, populates
 * the UserSession singleton with the logged-in user's information.
 */
public class LoginDialog extends JDialog {

    private JTextField tfUsername;
    private JPasswordField pfPassword;
    private boolean authenticated;

    /**
     * Constructs the login dialog.
     * @param parent The parent frame.
     */
    public LoginDialog(Frame parent) {
        super(parent, "Login", true);
        // Set up the panel with fields and labels
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints cs = new GridBagConstraints();
        cs.fill = GridBagConstraints.HORIZONTAL;

        cs.insets = new Insets(5, 5, 5, 5);

        // Username field
        JLabel lbUsername = new JLabel("Username: ");
        cs.gridx = 0;
        cs.gridy = 0;
        cs.gridwidth = 1;
        panel.add(lbUsername, cs);

        tfUsername = new JTextField(20);
        cs.gridx = 1;
        cs.gridy = 0;
        cs.gridwidth = 2;
        panel.add(tfUsername, cs);

        // Password field
        JLabel lbPassword = new JLabel("Password: ");
        cs.gridx = 0;
        cs.gridy = 1;
        cs.gridwidth = 1;
        panel.add(lbPassword, cs);

        pfPassword = new JPasswordField(20);
        cs.gridx = 1;
        cs.gridy = 1;
        cs.gridwidth = 2;
        panel.add(pfPassword, cs);
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        // Login button
        JButton btnLogin = new JButton("Login");
        btnLogin.addActionListener(e -> onLogin());

        // Cancel button
        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(e -> {
            authenticated = false;
            dispose();
        });

        // Panel for buttons
        JPanel bp = new JPanel();
        bp.add(btnLogin);
        bp.add(btnCancel);

        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(bp, BorderLayout.PAGE_END);

        pack();
        setResizable(false);
        setLocationRelativeTo(parent);
    }

    /**
     * Handles the login logic when the login button is clicked.
     */
    private void onLogin() {
        String username = tfUsername.getText();
        String password = new String(pfPassword.getPassword());

        UserDAO userDAO = new UserDAO();
        User user = userDAO.findByUsername(username);

        // IMPORTANT: In a real application, passwords should be hashed and salted.
        // This is a simplified check for demonstration purposes.
        if (user != null && user.getPassword().equals(password)) {
            // If login is successful, store user in session and check for forced password reset.
            UserSession session = UserSession.getInstance();
            session.setCurrentUser(user);

            // Fetch and store user permissions
            List<String> permissions = userDAO.getUserPermissions(user.getId());
            session.setUserPermissions(permissions);

            if (user.isRequiresPasswordReset()) {
                // If a password reset is required, show the reset dialog.
                ForcePasswordResetDialog resetDialog = new ForcePasswordResetDialog(this);
                resetDialog.setVisible(true);
                // The application will proceed only if the password was successfully reset.
                authenticated = resetDialog.isPasswordReset();
            } else {
                authenticated = true;
            }
            dispose(); // Close the login dialog
        } else {
            // If login fails, show an error message.
            JOptionPane.showMessageDialog(this, "Invalid username or password", "Login Error", JOptionPane.ERROR_MESSAGE);
            authenticated = false;
        }
    }

    /**
     * Checks if the user was successfully authenticated.
     * @return true if authentication was successful, false otherwise.
     */
    public boolean isAuthenticated() {
        return authenticated;
    }
} 