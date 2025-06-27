package com.university.app.ui;

import com.university.app.dao.UserDAO;
import com.university.app.dao.RoleDAO;
import com.university.app.dao.DepartmentDAO;
import com.university.app.dao.CourseDAO;
import com.university.app.dao.SectionDAO;
import com.university.app.dao.StudentDAO;
import com.university.app.model.User;
import com.university.app.model.Role;
import com.university.app.model.Department;
import com.university.app.model.Course;
import com.university.app.model.Section;
import com.university.app.model.Student;

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
    private JComboBox<Department> departmentComboBox;
    private JComboBox<Course> courseComboBox;
    private JComboBox<Section> sectionComboBox;
    private JComboBox<String> semesterComboBox;
    private JComboBox<Integer> yearComboBox;
    private JComboBox<Student> studentComboBox;

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

        // Department dropdown
        JLabel deptLabel = new JLabel("Department:");
        gbc.gridx = 0; gbc.gridy = 5;
        add(deptLabel, gbc);
        departmentComboBox = new JComboBox<>();
        for (Department d : new DepartmentDAO().getAllDepartments()) departmentComboBox.addItem(d);
        gbc.gridx = 1; gbc.gridy = 5;
        add(departmentComboBox, gbc);
        departmentComboBox.setEnabled(false);

        // Course dropdown
        JLabel courseLabel = new JLabel("Course:");
        gbc.gridx = 0; gbc.gridy = 6;
        add(courseLabel, gbc);
        courseComboBox = new JComboBox<>();
        gbc.gridx = 1; gbc.gridy = 6;
        add(courseComboBox, gbc);
        courseComboBox.setEnabled(false);

        // Section dropdown
        JLabel sectionLabel = new JLabel("Section:");
        gbc.gridx = 0; gbc.gridy = 7;
        add(sectionLabel, gbc);
        sectionComboBox = new JComboBox<>();
        gbc.gridx = 1; gbc.gridy = 7;
        add(sectionComboBox, gbc);
        sectionComboBox.setEnabled(false);

        // Semester dropdown
        JLabel semesterLabel = new JLabel("Semester:");
        gbc.gridx = 0; gbc.gridy = 8;
        add(semesterLabel, gbc);
        semesterComboBox = new JComboBox<>();
        gbc.gridx = 1; gbc.gridy = 8;
        add(semesterComboBox, gbc);
        semesterComboBox.setEnabled(false);

        // Year dropdown
        JLabel yearLabel = new JLabel("Year:");
        gbc.gridx = 0; gbc.gridy = 9;
        add(yearLabel, gbc);
        yearComboBox = new JComboBox<>();
        gbc.gridx = 1; gbc.gridy = 9;
        add(yearComboBox, gbc);
        yearComboBox.setEnabled(false);

        // Student dropdown
        JLabel studentLabel = new JLabel("Student:");
        gbc.gridx = 0; gbc.gridy = 10;
        add(studentLabel, gbc);
        studentComboBox = new JComboBox<>();
        gbc.gridx = 1; gbc.gridy = 10;
        add(studentComboBox, gbc);
        studentComboBox.setEnabled(false);

        JButton saveButton = new JButton("Save");
        saveButton.setToolTipText("Save the new user");
        saveButton.addActionListener(e -> saveUser());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(saveButton);
        gbc.gridx = 0; gbc.gridy = 11; gbc.gridwidth = 2;
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