package com.university.app.ui;

import com.university.app.dao.DepartmentDAO;
import com.university.app.dao.StudentDAO;
import com.university.app.model.Department;
import com.university.app.model.Student;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class AddStudentDialog extends JDialog {
    private JTextField idField, nameField, totalCreditsField;
    private JComboBox<Department> deptNameComboBox;

    public AddStudentDialog(Frame owner) {
        super(owner, "Add New Student", true);
        setLayout(new GridLayout(5, 2));

        add(new JLabel("ID:"));
        idField = new JTextField();
        add(idField);

        add(new JLabel("Name:"));
        nameField = new JTextField();
        add(nameField);

        add(new JLabel("Department Name:"));
        deptNameComboBox = new JComboBox<>();
        add(deptNameComboBox);
        
        add(new JLabel("Total Credits:"));
        totalCreditsField = new JTextField();
        add(totalCreditsField);
        
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> saveStudent());
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

    private void saveStudent() {
        try {
            String id = idField.getText();
            String name = nameField.getText();
            Department selectedDepartment = (Department) deptNameComboBox.getSelectedItem();
            String deptName = selectedDepartment.getDeptName();
            int totalCredits = Integer.parseInt(totalCreditsField.getText());

            Student student = new Student(id, name, deptName, totalCredits);
            StudentDAO studentDAO = new StudentDAO();
            studentDAO.addStudent(student);
            
            JOptionPane.showMessageDialog(this, "Student added successfully!");
            dispose();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for Total Credits.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error saving student: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
} 