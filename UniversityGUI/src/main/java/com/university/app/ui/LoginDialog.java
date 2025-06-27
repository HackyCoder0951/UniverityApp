package com.university.app.ui;

import com.university.app.dao.UserDAO;
import com.university.app.model.User;
import com.university.app.service.UserSession;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class LoginDialog extends JDialog {
    private JComboBox<User> userComboBox;
    private JPasswordField passwordField;
    private boolean authenticated = false;

    public LoginDialog(Frame owner) {
        super(owner, "Login", true);
        setLayout(new BorderLayout());

        userComboBox = new JComboBox<>();
        // Populate userComboBox with all users (UID - Username)
        List<User> users = new UserDAO().getAllUsers("");
        for (User user : users) {
            userComboBox.addItem(user);
        }
        userComboBox.setSelectedIndex(-1);
        passwordField = new JPasswordField();
        passwordField.setEnabled(false);
        userComboBox.addActionListener(e -> passwordField.setEnabled(userComboBox.getSelectedIndex() != -1));
        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("User (UID - Username):"));
        panel.add(userComboBox);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        add(panel, BorderLayout.CENTER);
        
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> handleAuthenticate());
        add(loginButton, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(owner);
    }

    private void handleAuthenticate() {
        if (authenticate()) {
            authenticated = true;
            UserSession.getInstance().setCurrentUser((User) userComboBox.getSelectedItem());
            dispose();

            // Force password change if required
            User user = (User) userComboBox.getSelectedItem();
            if (user.isRequiresPasswordReset()) {
                ForcePasswordResetDialog resetDialog = new ForcePasswordResetDialog(null);
                resetDialog.setVisible(true);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean authenticate() {
        User selectedUser = (User) userComboBox.getSelectedItem();
        if (selectedUser == null) return false;
        String password = new String(passwordField.getPassword());
        User user = new UserDAO().getUserByUsername(selectedUser.getUsername());
        return user != null && user.getPassword().equals(password);
    }

    public boolean isAuthenticated() {
        return authenticated;
    }
} 