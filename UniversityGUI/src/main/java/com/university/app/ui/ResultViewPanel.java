package com.university.app.ui;

import com.university.app.dao.ResultDAO;
import com.university.app.dao.StudentDAO;
import com.university.app.model.Result;
import com.university.app.model.Student;

import javax.swing.*;
// import javax.swing.table.DefaultTableModel;
import java.awt.*;
//import java.sql.SQLException;
import java.time.Year;
//import java.util.List;

public class ResultViewPanel extends JPanel {
    private JComboBox<String> studentCombo, semesterCombo, resultTypeCombo;
    private JComboBox<Integer> yearCombo;
    private JButton searchButton;
    private JTable resultTable;
    private ResultDAO resultDAO;
    private StudentDAO studentDAO;
    private java.util.Map<String, String> displayToIdMap;

    public ResultViewPanel() {
        super(new BorderLayout(10, 10));
        resultDAO = new ResultDAO();
        studentDAO = new StudentDAO();
        displayToIdMap = new java.util.HashMap<>();
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Student:"));
        java.util.List<Student> students = studentDAO.getAllStudents();
        java.util.List<String> displayList = new java.util.ArrayList<>();
        for (Student s : students) {
            String display = s.getId() + " - " + s.getName();
            displayList.add(display);
            displayToIdMap.put(display, s.getId());
        }
        studentCombo = new JComboBox<>(displayList.toArray(new String[0]));
        filterPanel.add(studentCombo);
        filterPanel.add(new JLabel("Semester:"));
        semesterCombo = new JComboBox<>(new String[]{"Spring", "Summer", "Fall", "Winter", "Annual"});
        filterPanel.add(semesterCombo);
        filterPanel.add(new JLabel("Year:"));
        yearCombo = new JComboBox<>();
        filterPanel.add(yearCombo);
        filterPanel.add(new JLabel("Type:"));
        resultTypeCombo = new JComboBox<>(new String[]{"semester", "annual", "final"});
        filterPanel.add(resultTypeCombo);
        searchButton = new JButton("Search");
        filterPanel.add(searchButton);
        add(filterPanel, BorderLayout.NORTH);

        resultTable = new JTable();
        add(new JScrollPane(resultTable), BorderLayout.CENTER);

        searchButton.addActionListener(e -> loadResults());
        studentCombo.addActionListener(e -> updateYearCombo());
        updateYearCombo();
    }

    private void loadResults() {
        try {
            String display = (String) studentCombo.getSelectedItem();
            String studentId = displayToIdMap.get(display);
            String semester = (String) semesterCombo.getSelectedItem();
            String resultType = (String) resultTypeCombo.getSelectedItem();
            int year = (Integer) yearCombo.getSelectedItem();
            java.util.List<Result> results = new ResultDAO().getResultsByType(studentId, resultType);
            javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(
                new String[]{"Semester", "Year", "SGPA", "CGPA", "Total Credits", "Type"}, 0);
            for (Result r : results) {
                if (r.getSemester().equalsIgnoreCase(semester) && r.getYear() == year) {
                    model.addRow(new Object[]{
                        r.getSemester(), r.getYear(), r.getSgpa(), r.getCgpa(), r.getTotalCredits(), r.getResultType()
                    });
                }
            }
            resultTable.setModel(model);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading results: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateYearCombo() {
        String display = (String) studentCombo.getSelectedItem();
        String studentId = displayToIdMap.get(display);
        java.util.List<Integer> years = new com.university.app.dao.ResultDAO().getAvailableYears(studentId);
        yearCombo.removeAllItems();
        for (Integer y : years) yearCombo.addItem(y);
        if (yearCombo.getItemCount() > 0) yearCombo.setSelectedIndex(0);
    }
} 