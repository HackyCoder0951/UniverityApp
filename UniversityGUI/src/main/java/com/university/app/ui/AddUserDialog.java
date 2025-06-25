package com.university.app.ui;

import com.university.app.dao.UserDAO;
import com.university.app.model.User;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class AddUserDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;

    public AddUserDialog(Frame owner) {
        super(owner, "Add New User", true);
        setLayout(new GridLayout(4, 4));

        add(new JLabel("Username:"));
        usernameField = new JTextField();
        add(usernameField);

        add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        add(passwordField);

        add(new JLabel("Role:"));
        roleComboBox = new JComboBox<>(new String[]{"entry", "reporting"});
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
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String role = (String) roleComboBox.getSelectedItem();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and password cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            UserDAO userDAO = new UserDAO();
            userDAO.addUser(new User(username, password, role, false));
            JOptionPane.showMessageDialog(this, "User added successfully!");
            dispose();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error saving user: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
} 