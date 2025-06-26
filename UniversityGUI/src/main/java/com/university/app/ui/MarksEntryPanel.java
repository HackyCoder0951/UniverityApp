package com.university.app.ui;

import com.university.app.dao.StudentDAO;
import com.university.app.dao.CourseDAO;
import com.university.app.dao.SectionDAO;
import com.university.app.dao.InstructorDAO;
import com.university.app.dao.MarksDAO;
import com.university.app.model.Student;
import com.university.app.model.Course;
import com.university.app.model.Section;
import com.university.app.model.Instructor;
import com.university.app.model.Marks;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
// import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarksEntryPanel extends JPanel {
    private JComboBox<String> studentCombo, courseCombo, sectionCombo, instructorCombo;
    private JTextField marksField, letterGradeField, gpaField;
    private JButton saveButton, clearButton;
    private Map<String, String> studentDisplayToId = new HashMap<>();
    private Map<String, String> courseDisplayToId = new HashMap<>();
    private Map<String, String> sectionDisplayToId = new HashMap<>();
    private Map<String, String> instructorDisplayToId = new HashMap<>();

    public MarksEntryPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        int row = 0;

        // Student Dropdown
        gbc.gridx = 0; gbc.gridy = row;
        add(new JLabel("Student:"), gbc);
        gbc.gridx = 1;
        studentCombo = new JComboBox<>(getStudentDisplayList());
        add(studentCombo, gbc);
        row++;

        // Course Dropdown
        gbc.gridx = 0; gbc.gridy = row;
        add(new JLabel("Course:"), gbc);
        gbc.gridx = 1;
        courseCombo = new JComboBox<>(getCourseDisplayList());
        add(courseCombo, gbc);
        row++;

        // Section Dropdown
        gbc.gridx = 0; gbc.gridy = row;
        add(new JLabel("Section:"), gbc);
        gbc.gridx = 1;
        sectionCombo = new JComboBox<>(getSectionDisplayList());
        add(sectionCombo, gbc);
        row++;

        // Instructor Dropdown
        gbc.gridx = 0; gbc.gridy = row;
        add(new JLabel("Instructor:"), gbc);
        gbc.gridx = 1;
        instructorCombo = new JComboBox<>(getInstructorDisplayList());
        add(instructorCombo, gbc);
        row++;

        // Marks Input
        gbc.gridx = 0; gbc.gridy = row;
        add(new JLabel("Marks:"), gbc);
        gbc.gridx = 1;
        marksField = new JTextField(10);
        add(marksField, gbc);
        row++;

        // Letter Grade (auto)
        gbc.gridx = 0; gbc.gridy = row;
        add(new JLabel("Letter Grade:"), gbc);
        gbc.gridx = 1;
        letterGradeField = new JTextField(10);
        letterGradeField.setEditable(false);
        add(letterGradeField, gbc);
        row++;

        // GPA (auto)
        gbc.gridx = 0; gbc.gridy = row;
        add(new JLabel("GPA:"), gbc);
        gbc.gridx = 1;
        gpaField = new JTextField(10);
        gpaField.setEditable(false);
        add(gpaField, gbc);
        row++;

        // Buttons
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        saveButton = new JButton("Save");
        clearButton = new JButton("Clear");
        buttonPanel.add(saveButton);
        buttonPanel.add(clearButton);
        add(buttonPanel, gbc);

        // Listeners
        marksField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                updateGradeAndGPA();
            }
        });
        saveButton.addActionListener(e -> saveMarks());
        clearButton.addActionListener(e -> clearFields());
    }

    private String[] getStudentDisplayList() {
        studentDisplayToId.clear();
        List<Student> students = new StudentDAO().getAllStudents();
        return students.stream().map(s -> {
            String display = s.getId() + " - " + s.getName();
            studentDisplayToId.put(display, s.getId());
            return display;
        }).toArray(String[]::new);
    }
    private String[] getCourseDisplayList() {
        courseDisplayToId.clear();
        List<Course> courses = new CourseDAO().getAllCourses();
        return courses.stream().map(c -> {
            String display = c.getCourseId() + " - " + c.getTitle();
            courseDisplayToId.put(display, c.getCourseId());
            return display;
        }).toArray(String[]::new);
    }
    private String[] getSectionDisplayList() {
        sectionDisplayToId.clear();
        List<Section> sections = new SectionDAO().getAllSections();
        return sections.stream().map(sec -> {
            String display = sec.getSecId() + " - " + sec.getCourseId() + " (" + sec.getSemester() + " " + sec.getYear() + ")";
            sectionDisplayToId.put(display, String.valueOf(sec.getSecId()));
            return display;
        }).toArray(String[]::new);
    }
    private String[] getInstructorDisplayList() {
        instructorDisplayToId.clear();
        List<Instructor> instructors = new InstructorDAO().getAllInstructors();
        return instructors.stream().map(i -> {
            String display = i.getId() + " - " + i.getName();
            instructorDisplayToId.put(display, i.getId());
            return display;
        }).toArray(String[]::new);
    }

    private void updateGradeAndGPA() {
        try {
            String marksText = marksField.getText();
            if (marksText == null || marksText.isEmpty()) {
                letterGradeField.setText("");
                gpaField.setText("");
                return;
            }
            double marks = Double.parseDouble(marksText);
            String grade;
            double gpa;
            if (marks >= 90) { grade = "A+"; gpa = 4.0; }
            else if (marks >= 80) { grade = "A"; gpa = 3.7; }
            else if (marks >= 70) { grade = "B"; gpa = 3.0; }
            else if (marks >= 60) { grade = "C"; gpa = 2.0; }
            else if (marks >= 50) { grade = "D"; gpa = 1.0; }
            else { grade = "F"; gpa = 0.0; }
            letterGradeField.setText(grade);
            gpaField.setText(String.valueOf(gpa));
        } catch (NumberFormatException ex) {
            letterGradeField.setText("");
            gpaField.setText("");
        }
    }

    private void saveMarks() {
        try {
            String studentDisplay = (String) studentCombo.getSelectedItem();
            String courseDisplay = (String) courseCombo.getSelectedItem();
            String sectionDisplay = (String) sectionCombo.getSelectedItem();
            String instructorDisplay = (String) instructorCombo.getSelectedItem();
            String marksText = marksField.getText();
            String grade = letterGradeField.getText();
            String gpaText = gpaField.getText();
            if (studentDisplay == null || courseDisplay == null || sectionDisplay == null || instructorDisplay == null || marksText.isEmpty() || grade.isEmpty() || gpaText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            double marks = Double.parseDouble(marksText);
            double gpa = Double.parseDouble(gpaText);
            Marks marksObj = new Marks();
            marksObj.setStudentId(studentDisplayToId.get(studentDisplay));
            marksObj.setCourseId(courseDisplayToId.get(courseDisplay));
            marksObj.setSectionId(sectionDisplayToId.get(sectionDisplay));
            marksObj.setInstructorId(instructorDisplayToId.get(instructorDisplay));
            marksObj.setMarks(marks);
            marksObj.setLetterGrade(grade);
            marksObj.setGpa(gpa);
            new MarksDAO().addMark(marksObj);
            JOptionPane.showMessageDialog(this, "Marks saved successfully!");
            clearFields();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving marks: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        studentCombo.setSelectedIndex(0);
        courseCombo.setSelectedIndex(0);
        sectionCombo.setSelectedIndex(0);
        instructorCombo.setSelectedIndex(0);
        marksField.setText("");
        letterGradeField.setText("");
        gpaField.setText("");
    }
}
