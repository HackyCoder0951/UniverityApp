package com.university.app.ui;

import com.university.app.dao.StudentDAO;
import com.university.app.dao.DepartmentDAO;
import com.university.app.model.Student;
import com.university.app.model.Department;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
// import java.util.List;

public class StudentPanel extends JPanel {
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private JButton addButton, updateButton, deleteButton;
    private StudentDAO studentDAO = new StudentDAO();

    public StudentPanel() {
        setLayout(new BorderLayout(10, 10));
        tableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Department", "Total Credits"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        studentTable = new JTable(tableModel);
        loadStudents();
        add(new JScrollPane(studentTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        addButton = new JButton("Add");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> openStudentDialog(null));
        updateButton.addActionListener(e -> {
            int row = studentTable.getSelectedRow();
            if (row >= 0) {
                Student s = getStudentFromRow(row);
                openStudentDialog(s);
            } else {
                JOptionPane.showMessageDialog(this, "Select a student to update.");
            }
        });
        deleteButton.addActionListener(e -> {
            int row = studentTable.getSelectedRow();
            if (row >= 0) {
                String id = (String) tableModel.getValueAt(row, 0);
                int confirm = JOptionPane.showConfirmDialog(this, "Delete student " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        studentDAO.deleteStudent(id);
                        loadStudents();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this, "Error deleting student: " + ex.getMessage());
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Select a student to delete.");
            }
        });
    }

    private void loadStudents() {
        tableModel.setRowCount(0);
        for (Student s : studentDAO.getAllStudents()) {
            tableModel.addRow(new Object[]{s.getId(), s.getName(), s.getDeptName(), s.getTotalCredits()});
        }
    }

    private Student getStudentFromRow(int row) {
        String id = (String) tableModel.getValueAt(row, 0);
        String name = (String) tableModel.getValueAt(row, 1);
        String dept = (String) tableModel.getValueAt(row, 2);
        int credits = (Integer) tableModel.getValueAt(row, 3);
        return new Student(id, name, dept, credits);
    }

    private void openStudentDialog(Student student) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), student == null ? "Add Student" : "Update Student", true);
        dialog.setLayout(new GridLayout(5, 2, 10, 10));
        JTextField idField = new JTextField(student == null ? "" : student.getId());
        JTextField nameField = new JTextField(student == null ? "" : student.getName());
        JComboBox<Department> deptCombo = new JComboBox<>();
        for (Department d : new DepartmentDAO().getAllDepartments()) deptCombo.addItem(d);
        if (student != null) {
            for (int i = 0; i < deptCombo.getItemCount(); i++) {
                if (deptCombo.getItemAt(i).getDeptName().equals(student.getDeptName())) {
                    deptCombo.setSelectedIndex(i);
                    break;
                }
            }
        }
        JTextField creditsField = new JTextField(student == null ? "" : String.valueOf(student.getTotalCredits()));
        dialog.add(new JLabel("ID:"));
        dialog.add(idField);
        dialog.add(new JLabel("Name:"));
        dialog.add(nameField);
        dialog.add(new JLabel("Department:"));
        dialog.add(deptCombo);
        dialog.add(new JLabel("Total Credits:"));
        dialog.add(creditsField);
        JButton saveBtn = new JButton("Save");
        dialog.add(saveBtn);
        JButton cancelBtn = new JButton("Cancel");
        dialog.add(cancelBtn);
        saveBtn.addActionListener(e -> {
            try {
                String id = idField.getText();
                String name = nameField.getText();
                Department dept = (Department) deptCombo.getSelectedItem();
                int credits = Integer.parseInt(creditsField.getText());
                Student s = new Student(id, name, dept.getDeptName(), credits);
                if (student == null) {
                    studentDAO.addStudent(s);
                } else {
                    studentDAO.updateStudent(s);
                }
                loadStudents();
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });
        cancelBtn.addActionListener(e -> dialog.dispose());
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
} 