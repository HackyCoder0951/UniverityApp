package com.university.app.ui;

import com.university.app.dao.UserDAO;
import com.university.app.dao.LoginHistoryDAO;
import com.university.app.model.User;
import com.university.app.service.UserSession;
import com.university.app.model.Role;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class LoginDialog extends JDialog {
    private JComboBox<User> userComboBox;
    private JPasswordField passwordField;
    private boolean authenticated = false;
    private JComboBox<Role> roleComboBox;
    private JTextField studentIdField;

    public LoginDialog(Frame owner) {
        super(owner, "Login", true);
        setLayout(new BorderLayout());

        roleComboBox = new JComboBox<>();
        for (Role role : new com.university.app.dao.RoleDAO().getAllRoles()) {
            roleComboBox.addItem(role);
        }
        roleComboBox.setSelectedIndex(-1);
        roleComboBox.addActionListener(e -> onRoleChanged());

        userComboBox = new JComboBox<>();
        // Populate userComboBox with all users (UID - Username)
        List<User> users = new UserDAO().getAllUsers("");
        for (User user : users) {
            userComboBox.addItem(user);
        }
        userComboBox.setSelectedIndex(-1);
        passwordField = new JPasswordField();
        passwordField.setEnabled(false);
        userComboBox.addActionListener(e -> onRoleChanged());
        studentIdField = new JTextField();
        studentIdField.setEnabled(false);

        JPanel panel = new JPanel(new GridLayout(4, 2));
        panel.add(new JLabel(" Role:"));
        panel.add(roleComboBox);
        panel.add(new JLabel(" User (UID - Username):"));
        panel.add(userComboBox);
        panel.add(new JLabel(" Student ID:"));
        panel.add(studentIdField);
        panel.add(new JLabel(" Password:"));
        panel.add(passwordField);
        add(panel, BorderLayout.CENTER);
        
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> handleAuthenticate());
        add(loginButton, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(owner);
        onRoleChanged();
    }

    private void onRoleChanged() {
        Role selectedRole = (Role) roleComboBox.getSelectedItem();
        boolean isStudent = selectedRole != null && selectedRole.getName().equalsIgnoreCase("student");
        userComboBox.setEnabled(!isStudent);
        studentIdField.setEnabled(isStudent);

        if (isStudent) {
            passwordField.setEnabled(true);
            passwordField.setEditable(true);
            userComboBox.setSelectedIndex(-1);
        } else {
            passwordField.setEnabled(userComboBox.getSelectedIndex() != -1);
            passwordField.setEditable(userComboBox.getSelectedIndex() != -1);
            studentIdField.setText("");
        }
    }

    private void handleAuthenticate() {
        if (authenticate()) {
            authenticated = true;
            Role selectedRole = (Role) roleComboBox.getSelectedItem();
            User user = null;
            if (selectedRole != null && selectedRole.getName().equalsIgnoreCase("student")) {
                String studentId = studentIdField.getText();
                user = new UserDAO().getUserByUsername(studentId);
            } else {
                user = (User) userComboBox.getSelectedItem();
            }
            UserSession.getInstance().setCurrentUser(user);
            try {
                new LoginHistoryDAO().logLogin(user.getUid(), user.getUsername());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            dispose();

            // Force password change if required
            if (user != null && user.isRequiresPasswordReset()) {
                ForcePasswordResetDialog resetDialog = new ForcePasswordResetDialog(null);
                resetDialog.setVisible(true);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean authenticate() {
        Role selectedRole = (Role) roleComboBox.getSelectedItem();
        if (selectedRole != null && selectedRole.getName().equalsIgnoreCase("student")) {
            String studentId = studentIdField.getText();
            if (studentId == null || studentId.isEmpty()) return false;
            String password = new String(passwordField.getPassword());
            User user = new UserDAO().getUserByUsername(studentId);
            return user != null && user.getPassword().equals(password) && user.getRole().equalsIgnoreCase("student");
        } else {
            User selectedUser = (User) userComboBox.getSelectedItem();
            if (selectedUser == null) return false;
            String password = new String(passwordField.getPassword());
            User user = new UserDAO().getUserByUsername(selectedUser.getUsername());
            return user != null && user.getPassword().equals(password);
        }
    }

    public boolean isAuthenticated() {
        return authenticated;
    }
} 