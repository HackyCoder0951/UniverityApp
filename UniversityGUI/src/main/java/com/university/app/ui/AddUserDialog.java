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
    private JComboBox<String> studentComboBox;
    private JComboBox<Department> departmentComboBox;
    private JComboBox<String> courseComboBox;
    private JComboBox<String> sectionComboBox;
    private JComboBox<String> semesterComboBox;
    private JComboBox<Integer> yearComboBox;

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
        usernameField.setPreferredSize(new Dimension(200, 28));
        gbc.gridx = 1; gbc.gridy = 0;
        add(usernameField, gbc);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setToolTipText("Enter a secure password");
        gbc.gridx = 0; gbc.gridy = 1;
        add(passwordLabel, gbc);
        passwordField = new JPasswordField(16);
        passwordField.setToolTipText("Enter a secure password");
        passwordField.setPreferredSize(new Dimension(200, 28));
        gbc.gridx = 1; gbc.gridy = 1;
        add(passwordField, gbc);

        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setToolTipText("Select the user's role");
        gbc.gridx = 0; gbc.gridy = 2;
        add(roleLabel, gbc);
        roleComboBox = new JComboBox<>();
        roleComboBox.setPreferredSize(new Dimension(200, 28));
        for (Role role : new RoleDAO().getAllRoles()) {
            roleComboBox.addItem(role);
        }
        roleComboBox.setToolTipText("Select the user's role");
        gbc.gridx = 1; gbc.gridy = 2;
        add(roleComboBox, gbc);
        roleComboBox.addActionListener(e -> onRoleChanged());

        // Department dropdown
        JLabel deptLabel = new JLabel("Department:");
        gbc.gridx = 0; gbc.gridy = 5;
        add(deptLabel, gbc);
        departmentComboBox = new JComboBox<>();
        departmentComboBox.setPreferredSize(new Dimension(200, 28));
        gbc.gridx = 1; gbc.gridy = 5;
        add(departmentComboBox, gbc);
        departmentComboBox.setEnabled(false);
        departmentComboBox.addActionListener(e -> onDepartmentChanged());

        // Course dropdown
        JLabel courseLabel = new JLabel("Course:");
        gbc.gridx = 0; gbc.gridy = 6;
        add(courseLabel, gbc);
        courseComboBox = new JComboBox<>();
        courseComboBox.setPreferredSize(new Dimension(200, 28));
        gbc.gridx = 1; gbc.gridy = 6;
        add(courseComboBox, gbc);
        courseComboBox.setEnabled(false);
        courseComboBox.addActionListener(e -> onCourseChanged());

        // Section dropdown
        JLabel sectionLabel = new JLabel("Section:");
        gbc.gridx = 0; gbc.gridy = 7;
        add(sectionLabel, gbc);
        sectionComboBox = new JComboBox<>();
        sectionComboBox.setPreferredSize(new Dimension(200, 28));
        gbc.gridx = 1; gbc.gridy = 7;
        add(sectionComboBox, gbc);
        sectionComboBox.setEnabled(false);
        sectionComboBox.addActionListener(e -> onSectionChanged());

        // Semester dropdown
        JLabel semesterLabel = new JLabel("Semester:");
        gbc.gridx = 0; gbc.gridy = 8;
        add(semesterLabel, gbc);
        semesterComboBox = new JComboBox<>();
        semesterComboBox.setPreferredSize(new Dimension(200, 28));
        gbc.gridx = 1; gbc.gridy = 8;
        add(semesterComboBox, gbc);
        semesterComboBox.setEnabled(false);
        semesterComboBox.addActionListener(e -> onSemesterChanged());

        // Year dropdown
        JLabel yearLabel = new JLabel("Year:");
        gbc.gridx = 0; gbc.gridy = 9;
        add(yearLabel, gbc);
        yearComboBox = new JComboBox<>();
        yearComboBox.setPreferredSize(new Dimension(200, 28));
        gbc.gridx = 1; gbc.gridy = 9;
        add(yearComboBox, gbc);
        yearComboBox.setEnabled(false);
        yearComboBox.addActionListener(e -> onYearChanged());

        // Student dropdown
        JLabel studentLabel = new JLabel("Student:");
        gbc.gridx = 0; gbc.gridy = 10;
        add(studentLabel, gbc);
        studentComboBox = new JComboBox<>();
        studentComboBox.setPreferredSize(new Dimension(200, 28));
        gbc.gridx = 1; gbc.gridy = 10;
        add(studentComboBox, gbc);
        studentComboBox.setEnabled(false);
        studentComboBox.addActionListener(e -> onStudentSelected());

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

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and password cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            UserDAO userDAO = new UserDAO();
            String userId = username;
            if (selectedRole != null && selectedRole.getName().equalsIgnoreCase("student")) {
                String selectedStudent = (String) studentComboBox.getSelectedItem();
                if (selectedStudent == null || selectedStudent.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please select a student.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                userId = selectedStudent.split(" - ")[0];
                username = userId; // Student login uses student ID
            }
            User user = new User(null, username, password, String.valueOf(selectedRole.getId()), false);
            userDAO.addUser(user);
            JOptionPane.showMessageDialog(this, "User added successfully!");
            dispose();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error saving user: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void onRoleChanged() {
        Role selectedRole = (Role) roleComboBox.getSelectedItem();
        boolean isStudent = selectedRole != null && selectedRole.getName().equalsIgnoreCase("student");
        departmentComboBox.setEnabled(isStudent);
        courseComboBox.setEnabled(false);
        sectionComboBox.setEnabled(false);
        semesterComboBox.setEnabled(false);
        yearComboBox.setEnabled(false);
        studentComboBox.setEnabled(false);
        if (isStudent) {
            departmentComboBox.removeAllItems();
            for (Department d : new DepartmentDAO().getAllDepartments()) {
                departmentComboBox.addItem(d);
            }
        } else {
            departmentComboBox.removeAllItems();
            courseComboBox.removeAllItems();
            sectionComboBox.removeAllItems();
            semesterComboBox.removeAllItems();
            yearComboBox.removeAllItems();
            studentComboBox.removeAllItems();
        }
    }

    private void onDepartmentChanged() {
        Department selectedDept = (Department) departmentComboBox.getSelectedItem();
        courseComboBox.setEnabled(selectedDept != null);
        courseComboBox.removeAllItems();
        sectionComboBox.setEnabled(false);
        sectionComboBox.removeAllItems();
        semesterComboBox.setEnabled(false);
        semesterComboBox.removeAllItems();
        yearComboBox.setEnabled(false);
        yearComboBox.removeAllItems();
        studentComboBox.setEnabled(false);
        studentComboBox.removeAllItems();
        if (selectedDept != null) {
            for (Course c : new CourseDAO().getAllCourses()) {
                if (c.getDeptName().equals(selectedDept.getDeptName())) {
                    courseComboBox.addItem(c.getCourseId() + " - " + c.getTitle());
                }
            }
        }
    }

    private void onCourseChanged() {
        String selectedCourseStr = (String) courseComboBox.getSelectedItem();
        sectionComboBox.setEnabled(selectedCourseStr != null);
        sectionComboBox.removeAllItems();
        semesterComboBox.setEnabled(false);
        semesterComboBox.removeAllItems();
        yearComboBox.setEnabled(false);
        yearComboBox.removeAllItems();
        studentComboBox.setEnabled(false);
        studentComboBox.removeAllItems();
        if (selectedCourseStr != null) {
            String courseId = selectedCourseStr.split(" - ")[0];
            for (Section s : new SectionDAO().getAllSections()) {
                if (s.getCourseId().equals(courseId)) {
                    sectionComboBox.addItem(s.getSecId() + " - " + s.getCourseId() + " (" + s.getSemester() + " " + s.getYear() + ")");
                }
            }
        }
    }

    private void onSectionChanged() {
        String selectedSectionStr = (String) sectionComboBox.getSelectedItem();
        semesterComboBox.setEnabled(selectedSectionStr != null);
        semesterComboBox.removeAllItems();
        yearComboBox.setEnabled(false);
        yearComboBox.removeAllItems();
        studentComboBox.setEnabled(false);
        studentComboBox.removeAllItems();
        if (selectedSectionStr != null) {
            String[] parts = selectedSectionStr.split(" - | \\(");
            int secId = Integer.parseInt(parts[0]);
            String courseId = parts[1].split(" \\(")[0];
            Section selectedSection = new SectionDAO().getAllSections().stream()
                .filter(s -> s.getSecId() == secId && s.getCourseId().equals(courseId))
                .findFirst().orElse(null);
            if (selectedSection != null) {
                semesterComboBox.addItem(selectedSection.getSemester());
                yearComboBox.setEnabled(true);
                yearComboBox.removeAllItems();
                yearComboBox.addItem(selectedSection.getYear());
            }
        }
    }

    private void onSemesterChanged() {
        String selectedSectionStr = (String) sectionComboBox.getSelectedItem();
        String selectedSemester = (String) semesterComboBox.getSelectedItem();
        yearComboBox.setEnabled(selectedSemester != null);
        yearComboBox.removeAllItems();
        studentComboBox.setEnabled(false);
        studentComboBox.removeAllItems();
        if (selectedSectionStr != null && selectedSemester != null) {
            // Parse section string to get secId and courseId
            String[] parts = selectedSectionStr.split(" - | \\(");
            int secId = Integer.parseInt(parts[0]);
            String courseId = parts[1].split(" \\(")[0];
            Section selectedSection = new SectionDAO().getAllSections().stream()
                .filter(s -> s.getSecId() == secId && s.getCourseId().equals(courseId))
                .findFirst().orElse(null);
            if (selectedSection != null) {
                yearComboBox.addItem(selectedSection.getYear());
            }
        }
    }

    private void onYearChanged() {
        String selectedSectionStr = (String) sectionComboBox.getSelectedItem();
        String selectedSemester = (String) semesterComboBox.getSelectedItem();
        Integer selectedYear = (Integer) yearComboBox.getSelectedItem();
        studentComboBox.setEnabled(selectedSectionStr != null && selectedSemester != null && selectedYear != null);
        studentComboBox.removeAllItems();
        if (selectedSectionStr != null && selectedSemester != null && selectedYear != null) {
            String[] parts = selectedSectionStr.split(" - | \\(");
            int secId = Integer.parseInt(parts[0]);
            String courseId = parts[1].split(" \\(")[0];
            for (com.university.app.model.Takes t : new com.university.app.dao.TakesDAO().getAllTakes()) {
                if (t.getCourseId().equals(courseId) &&
                    t.getSecId() == secId &&
                    t.getSemester().equals(selectedSemester) &&
                    t.getYear() == selectedYear) {
                    Student s = new StudentDAO().getAllStudents().stream()
                        .filter(st -> st.getId().equals(t.getStudentId()))
                        .findFirst().orElse(null);
                    if (s != null) {
                        studentComboBox.addItem(s.getId() + " - " + s.getName());
                    }
                }
            }
        }
    }

    private void onStudentSelected() {
        Role selectedRole = (Role) roleComboBox.getSelectedItem();
        if (selectedRole != null && selectedRole.getName().equalsIgnoreCase("student")) {
            String selectedStudent = (String) studentComboBox.getSelectedItem();
            if (selectedStudent != null && !selectedStudent.isEmpty()) {
                saveUser();
            }
        }
    }
} 