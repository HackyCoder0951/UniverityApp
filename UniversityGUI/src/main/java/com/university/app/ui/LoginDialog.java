package com.university.app.ui;

import com.university.app.dao.UserDAO;
import com.university.app.model.User;
import com.university.app.service.UserSession;

import javax.swing.*;
import java.awt.*;

public class LoginDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private boolean authenticated = false;

    public LoginDialog(Frame owner) {
        super(owner, "Login", true);
        setLayout(new GridLayout(3, 2));

        add(new JLabel("Username:"));
        usernameField = new JTextField();
        add(usernameField);

        add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        add(passwordField);
        
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> authenticate());
        add(loginButton);
        
        pack();
        setLocationRelativeTo(owner);
    }

    private void authenticate() {
        UserDAO userDAO = new UserDAO();
        User user = userDAO.getUserByUsername(usernameField.getText());

        if (user != null && user.getPassword().equals(new String(passwordField.getPassword()))) {
            authenticated = true;
            UserSession.getInstance().setCurrentUser(user);
            dispose();

            // Force password change if required
            if (user.isRequiresPasswordReset()) {
                ForcePasswordResetDialog resetDialog = new ForcePasswordResetDialog(null);
                resetDialog.setVisible(true);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isAuthenticated() {
        return authenticated;
    }
} 