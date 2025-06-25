package com.university.app.ui;

import com.university.app.dao.DepartmentDAO;
import com.university.app.dao.InstructorDAO;
import com.university.app.model.Department;
import com.university.app.model.Instructor;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class AddInstructorDialog extends JDialog {
    private JTextField idField, nameField, salaryField;
    private JComboBox<Department> deptNameComboBox;

    public AddInstructorDialog(Frame owner) {
        super(owner, "Add New Instructor", true);
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

        add(new JLabel("Salary:"));
        salaryField = new JTextField();
        add(salaryField);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> saveInstructor());
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

    private void saveInstructor() {
        try {
            String id = idField.getText();
            String name = nameField.getText();
            Department selectedDepartment = (Department) deptNameComboBox.getSelectedItem();
            String deptName = selectedDepartment.getDeptName();
            double salary = Double.parseDouble(salaryField.getText());

            Instructor instructor = new Instructor(id, name, deptName, salary);
            InstructorDAO instructorDAO = new InstructorDAO();
            instructorDAO.addInstructor(instructor);

            JOptionPane.showMessageDialog(this, "Instructor added successfully!");
            dispose();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for Salary.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error saving instructor: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
} 