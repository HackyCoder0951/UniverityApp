package com.university.app.ui;

import com.university.app.dao.ResultDAO;
import com.university.app.model.Result;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class ResultViewPanel extends JPanel {
    private JComboBox<String> studentCombo, semesterCombo, resultTypeCombo;
    private JTextField yearField;
    private JButton searchButton;
    private JTable resultTable;
    private ResultDAO resultDAO;

    public ResultViewPanel(List<String> students) {
        super(new BorderLayout(10, 10));
        resultDAO = new ResultDAO();
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Student:"));
        studentCombo = new JComboBox<>(students.toArray(new String[0]));
        filterPanel.add(studentCombo);
        filterPanel.add(new JLabel("Semester:"));
        semesterCombo = new JComboBox<>(new String[]{"Spring", "Summer", "Fall", "Winter", "Annual"});
        filterPanel.add(semesterCombo);
        filterPanel.add(new JLabel("Year:"));
        yearField = new JTextField(6);
        filterPanel.add(yearField);
        filterPanel.add(new JLabel("Type:"));
        resultTypeCombo = new JComboBox<>(new String[]{"semester", "annual", "final"});
        filterPanel.add(resultTypeCombo);
        searchButton = new JButton("Search");
        filterPanel.add(searchButton);
        add(filterPanel, BorderLayout.NORTH);

        resultTable = new JTable();
        add(new JScrollPane(resultTable), BorderLayout.CENTER);

        searchButton.addActionListener(e -> loadResults());
    }

    private void loadResults() {
        try {
            String studentId = (String) studentCombo.getSelectedItem();
            String semester = (String) semesterCombo.getSelectedItem();
            String resultType = (String) resultTypeCombo.getSelectedItem();
            int year = Integer.parseInt(yearField.getText());
            List<Result> results = new ResultDAO().getResultsByType(studentId, resultType);
            DefaultTableModel model = new DefaultTableModel(
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
} 