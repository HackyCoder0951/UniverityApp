package com.university.app.ui;

import com.university.app.dao.CourseDAO;
import com.university.app.dao.DepartmentDAO;
import com.university.app.model.Course;
import com.university.app.model.Department;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class AddCourseDialog extends JDialog {
    private JTextField courseIdField, titleField, creditsField;
    private JComboBox<Department> deptNameComboBox;


    public AddCourseDialog(Frame owner) {
        super(owner, "Add New Course", true);
        setLayout(new GridLayout(5, 2));

        add(new JLabel("Course ID:"));
        courseIdField = new JTextField();
        add(courseIdField);

        add(new JLabel("Title:"));
        titleField = new JTextField();
        add(titleField);

        add(new JLabel("Department Name:"));
        deptNameComboBox = new JComboBox<>();
        add(deptNameComboBox);

        add(new JLabel("Credits:"));
        creditsField = new JTextField();
        add(creditsField);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> saveCourse());
        add(saveButton);

        loadDepartments();
        pack();
        setLocationRelativeTo(owner);
    }

    private void loadDepartments() {
        DepartmentDAO departmentDAO = new DepartmentDAO();
        List<Department> departments = departmentDAO.getAllDepartments();
        for (Department department : departments) {
            deptNameComboBox.addItem(department);
        }
    }

    private void saveCourse() {
        try {
            String courseId = courseIdField.getText();
            String title = titleField.getText();
            Department selectedDepartment = (Department) deptNameComboBox.getSelectedItem();
            String deptName = selectedDepartment.getDeptName();
            int credits = Integer.parseInt(creditsField.getText());

            Course course = new Course(courseId, title, deptName, credits);
            CourseDAO courseDAO = new CourseDAO();
            courseDAO.addCourse(course);

            JOptionPane.showMessageDialog(this, "Course added successfully!");
            dispose();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for Credits.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error saving course: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
} 