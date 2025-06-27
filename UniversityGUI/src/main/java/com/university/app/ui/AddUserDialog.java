package com.university.app.ui;

import com.university.app.dao.UserDAO;
import com.university.app.dao.RoleDAO;
import com.university.app.model.User;
import com.university.app.model.Role;

import javax.swing.*;
import java.awt.*;
// import java.awt.event.KeyAdapter;
// import java.awt.event.KeyEvent;
import java.sql.SQLException;

public class AddUserDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<Role> roleComboBox;
    private JTextField uidField;

    public AddUserDialog(Frame owner) {
        super(owner, "Add New User", true);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setToolTipText("Enter the user's login name");
        gbc.gridx = 0; gbc.gridy = 0;
        add(usernameLabel, gbc);
        usernameField = new JTextField(16);
        usernameField.setToolTipText("Enter the user's login name");
        gbc.gridx = 1; gbc.gridy = 0;
        add(usernameField, gbc);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setToolTipText("Enter a secure password");
        gbc.gridx = 0; gbc.gridy = 1;
        add(passwordLabel, gbc);
        passwordField = new JPasswordField(16);
        passwordField.setToolTipText("Enter a secure password");
        gbc.gridx = 1; gbc.gridy = 1;
        add(passwordField, gbc);

        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setToolTipText("Select the user's role");
        gbc.gridx = 0; gbc.gridy = 2;
        add(roleLabel, gbc);
        roleComboBox = new JComboBox<>();
        for (Role role : new RoleDAO().getAllRoles()) {
            roleComboBox.addItem(role);
        }
        roleComboBox.setToolTipText("Select the user's role");
        gbc.gridx = 1; gbc.gridy = 2;
        add(roleComboBox, gbc);

        JLabel uidLabel = new JLabel("UID:");
        uidLabel.setToolTipText("Enter a unique user ID");
        gbc.gridx = 0; gbc.gridy = 3;
        add(uidLabel, gbc);
        uidField = new JTextField(16);
        uidField.setToolTipText("Enter a unique user ID");
        gbc.gridx = 1; gbc.gridy = 3;
        add(uidField, gbc);

        JButton saveButton = new JButton("Save");
        saveButton.setToolTipText("Save the new user");
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
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        Role selectedRole = (Role) roleComboBox.getSelectedItem();
        String uid = uidField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and password cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            UserDAO userDAO = new UserDAO();
            User user = new User(uid, username, password, String.valueOf(selectedRole.getId()), false);
            userDAO.addUser(user);
            JOptionPane.showMessageDialog(this, "User added successfully!");
            dispose();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error saving user: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
} 