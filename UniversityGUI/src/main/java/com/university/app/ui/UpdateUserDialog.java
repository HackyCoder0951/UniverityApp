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

public class UpdateUserDialog extends JDialog {
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
    private JTextField studentIdField;
    private UserDAO userDAO;
    private User userToUpdate;

    public UpdateUserDialog(Frame owner, User userToUpdate) {
        super(owner, "Update User", true);
        this.userDAO = new UserDAO();
        this.userToUpdate = userToUpdate;
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setToolTipText("Edit the user's login name");
        gbc.gridx = 0; gbc.gridy = 0;
        add(usernameLabel, gbc);
        usernameField = new JTextField(userToUpdate.getUsername(), 16);
        usernameField.setToolTipText("Edit the user's login name");
        gbc.gridx = 1; gbc.gridy = 0;
        add(usernameField, gbc);

        JLabel passwordLabel = new JLabel("Password (leave blank to keep current):");
        passwordLabel.setToolTipText("Leave blank to keep the current password");
        gbc.gridx = 0; gbc.gridy = 1;
        add(passwordLabel, gbc);
        passwordField = new JPasswordField(16);
        passwordField.setToolTipText("Leave blank to keep the current password");
        gbc.gridx = 1; gbc.gridy = 1;
        add(passwordField, gbc);

        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setToolTipText("Select the user's role");
        gbc.gridx = 0; gbc.gridy = 2;
        add(roleLabel, gbc);
        roleComboBox = new JComboBox<>();
        java.util.List<Role> roles = new RoleDAO().getAllRoles();
        for (Role role : roles) {
            roleComboBox.addItem(role);
            if (role.getName().equalsIgnoreCase(userToUpdate.getRole())) {
                roleComboBox.setSelectedItem(role);
            }
        }
        roleComboBox.setToolTipText("Select the user's role");
        gbc.gridx = 1; gbc.gridy = 2;
        add(roleComboBox, gbc);
        roleComboBox.addActionListener(e -> onRoleChanged());

        JLabel uidLabel = new JLabel("UID:");
        uidLabel.setToolTipText("User ID (not editable)");
        gbc.gridx = 0; gbc.gridy = 3;
        add(uidLabel, gbc);
        uidField = new JTextField(userToUpdate.getUid(), 16);
        uidField.setEditable(false);
        uidField.setBackground(new Color(235, 235, 235));
        uidField.setToolTipText("User ID (not editable)");
        gbc.gridx = 1; gbc.gridy = 3;
        add(uidField, gbc);

        // Student ID (read-only, only for student users)
        JLabel studentIdLabel = new JLabel("Student ID:");
        studentIdLabel.setToolTipText("Student ID (not editable)");
        gbc.gridx = 0; gbc.gridy = 4;
        studentIdField = new JTextField(userToUpdate.getStudentId() != null ? userToUpdate.getStudentId() : "", 16);
        studentIdField.setEditable(false);
        studentIdField.setBackground(new Color(235, 235, 235));
        studentIdField.setToolTipText("Student ID (not editable)");
        add(studentIdLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 4;
        add(studentIdField, gbc);
        boolean isStudent = userToUpdate.getRole().equalsIgnoreCase("student");
        studentIdLabel.setVisible(isStudent);
        studentIdField.setVisible(isStudent);

        // Department dropdown
        JLabel deptLabel = new JLabel("Department:");
        gbc.gridx = 0; gbc.gridy = 5;
        add(deptLabel, gbc);
        departmentComboBox = new JComboBox<>();
        for (Department d : new DepartmentDAO().getAllDepartments()) departmentComboBox.addItem(d);
        gbc.gridx = 1; gbc.gridy = 5;
        add(departmentComboBox, gbc);
        departmentComboBox.setEnabled(false);
        departmentComboBox.addActionListener(e -> onDepartmentChanged());

        // Course dropdown
        JLabel courseLabel = new JLabel("Course:");
        gbc.gridx = 0; gbc.gridy = 6;
        add(courseLabel, gbc);
        courseComboBox = new JComboBox<>();
        gbc.gridx = 1; gbc.gridy = 6;
        add(courseComboBox, gbc);
        courseComboBox.setEnabled(false);
        courseComboBox.addActionListener(e -> onCourseChanged());

        // Section dropdown
        JLabel sectionLabel = new JLabel("Section:");
        gbc.gridx = 0; gbc.gridy = 7;
        add(sectionLabel, gbc);
        sectionComboBox = new JComboBox<>();
        gbc.gridx = 1; gbc.gridy = 7;
        add(sectionComboBox, gbc);
        sectionComboBox.setEnabled(false);
        sectionComboBox.addActionListener(e -> onSectionChanged());

        // Semester dropdown
        JLabel semesterLabel = new JLabel("Semester:");
        gbc.gridx = 0; gbc.gridy = 8;
        add(semesterLabel, gbc);
        semesterComboBox = new JComboBox<>();
        gbc.gridx = 1; gbc.gridy = 8;
        add(semesterComboBox, gbc);
        semesterComboBox.setEnabled(false);
        semesterComboBox.addActionListener(e -> onSemesterChanged());

        // Year dropdown
        JLabel yearLabel = new JLabel("Year:");
        gbc.gridx = 0; gbc.gridy = 9;
        add(yearLabel, gbc);
        yearComboBox = new JComboBox<>();
        gbc.gridx = 1; gbc.gridy = 9;
        add(yearComboBox, gbc);
        yearComboBox.setEnabled(false);
        yearComboBox.addActionListener(e -> onYearChanged());

        // Student dropdown
        JLabel studentLabel = new JLabel("Student:");
        gbc.gridx = 0; gbc.gridy = 10;
        add(studentLabel, gbc);
        studentComboBox = new JComboBox<>();
        gbc.gridx = 1; gbc.gridy = 10;
        add(studentComboBox, gbc);
        studentComboBox.setEnabled(false);

        JButton saveButton = new JButton("Save");
        saveButton.setToolTipText("Save changes to the user");
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
        String newUsername = usernameField.getText();
        String newPassword = new String(passwordField.getPassword());
        Role selectedRole = (Role) roleComboBox.getSelectedItem();
        String uid = uidField.getText();
        String studentId = studentIdField.getText();

        if (newUsername.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String passwordToSave = newPassword.isEmpty() ? userToUpdate.getPassword() : newPassword;
        User updatedUser = new User(uid, newUsername, passwordToSave, String.valueOf(selectedRole.getId()), userToUpdate.isRequiresPasswordReset(), studentId);

        try {
            userDAO.updateUser(userToUpdate.getUsername(), updatedUser);
            JOptionPane.showMessageDialog(this, "User updated successfully!");
            dispose();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating user: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
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
                    courseComboBox.addItem(c);
                }
            }
        }
    }

    private void onCourseChanged() {
        Course selectedCourse = (Course) courseComboBox.getSelectedItem();
        sectionComboBox.setEnabled(selectedCourse != null);
        sectionComboBox.removeAllItems();
        semesterComboBox.setEnabled(false);
        semesterComboBox.removeAllItems();
        yearComboBox.setEnabled(false);
        yearComboBox.removeAllItems();
        studentComboBox.setEnabled(false);
        studentComboBox.removeAllItems();
        if (selectedCourse != null) {
            for (Section s : new SectionDAO().getAllSections()) {
                if (s.getCourseId().equals(selectedCourse.getCourseId())) {
                    sectionComboBox.addItem(s);
                }
            }
        }
    }

    private void onSectionChanged() {
        Section selectedSection = (Section) sectionComboBox.getSelectedItem();
        semesterComboBox.setEnabled(selectedSection != null);
        semesterComboBox.removeAllItems();
        yearComboBox.setEnabled(false);
        yearComboBox.removeAllItems();
        studentComboBox.setEnabled(false);
        studentComboBox.removeAllItems();
        if (selectedSection != null) {
            semesterComboBox.addItem(selectedSection.getSemester());
            yearComboBox.setEnabled(true);
            yearComboBox.removeAllItems();
            yearComboBox.addItem(selectedSection.getYear());
        }
    }

    private void onSemesterChanged() {
        Section selectedSection = (Section) sectionComboBox.getSelectedItem();
        String selectedSemester = (String) semesterComboBox.getSelectedItem();
        yearComboBox.setEnabled(selectedSemester != null);
        yearComboBox.removeAllItems();
        studentComboBox.setEnabled(false);
        studentComboBox.removeAllItems();
        if (selectedSection != null && selectedSemester != null) {
            yearComboBox.addItem(selectedSection.getYear());
        }
    }

    private void onYearChanged() {
        Section selectedSection = (Section) sectionComboBox.getSelectedItem();
        String selectedSemester = (String) semesterComboBox.getSelectedItem();
        Integer selectedYear = (Integer) yearComboBox.getSelectedItem();
        studentComboBox.setEnabled(selectedSection != null && selectedSemester != null && selectedYear != null);
        studentComboBox.removeAllItems();
        if (selectedSection != null && selectedSemester != null && selectedYear != null) {
            for (com.university.app.model.Takes t : new com.university.app.dao.TakesDAO().getAllTakes()) {
                if (t.getCourseId().equals(selectedSection.getCourseId()) &&
                    t.getSecId() == selectedSection.getSecId() &&
                    t.getSemester().equals(selectedSemester) &&
                    t.getYear() == selectedYear) {
                    Student s = new StudentDAO().getAllStudents().stream()
                        .filter(st -> st.getId().equals(t.getStudentId()))
                        .findFirst().orElse(null);
                    if (s != null) {
                        studentComboBox.addItem(s);
                    }
                }
            }
        }
    }
} 