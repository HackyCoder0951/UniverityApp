package com.university.app.dao;

import com.university.app.model.Marks;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MarksDAO {
    private Connection conn;

    public MarksDAO() {
        this.conn = DatabaseConnector.getConnection();
    }

    public void addMark(Marks mark) throws SQLException {
        String sql = "INSERT INTO marks (student_id, course_id, section_id, instructor_id, semester, year, marks, letter_grade, gpa) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, mark.getStudentId());
            stmt.setString(2, mark.getCourseId());
            stmt.setString(3, mark.getSectionId());
            stmt.setString(4, mark.getInstructorId());
            stmt.setString(5, mark.getSemester());
            stmt.setInt(6, mark.getYear());
            stmt.setDouble(7, mark.getMarks());
            stmt.setString(8, mark.getLetterGrade());
            stmt.setDouble(9, mark.getGpa());
            stmt.executeUpdate();
        }
    }

    public void updateMark(Marks mark) throws SQLException {
        String sql = "UPDATE marks SET marks=?, letter_grade=?, gpa=?, updated_at=NOW() WHERE mark_id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, mark.getMarks());
            stmt.setString(2, mark.getLetterGrade());
            stmt.setDouble(3, mark.getGpa());
            stmt.setInt(4, mark.getMarkId());
            stmt.executeUpdate();
        }
    }

    public void deleteMark(int markId) throws SQLException {
        String sql = "DELETE FROM marks WHERE mark_id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, markId);
            stmt.executeUpdate();
        }
    }

    public Marks getMarkById(int markId) throws SQLException {
        String sql = "SELECT * FROM marks WHERE mark_id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, markId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractMark(rs);
                }
            }
        }
        return null;
    }

    public List<Marks> getMarksForStudent(String studentId) throws SQLException {
        String sql = "SELECT * FROM marks WHERE student_id=? ORDER BY year DESC, semester DESC";
        List<Marks> marksList = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    marksList.add(extractMark(rs));
                }
            }
        }
        return marksList;
    }

    public List<Marks> getMarksForSection(String courseId, String sectionId, String semester, int year) throws SQLException {
        String sql = "SELECT * FROM marks WHERE course_id=? AND section_id=? AND semester=? AND year=?";
        List<Marks> marksList = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, courseId);
            stmt.setString(2, sectionId);
            stmt.setString(3, semester);
            stmt.setInt(4, year);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    marksList.add(extractMark(rs));
                }
            }
        }
        return marksList;
    }

    private Marks extractMark(ResultSet rs) throws SQLException {
        Marks mark = new Marks();
        mark.setMarkId(rs.getInt("mark_id"));
        mark.setStudentId(rs.getString("student_id"));
        mark.setCourseId(rs.getString("course_id"));
        mark.setSectionId(rs.getString("section_id"));
        mark.setInstructorId(rs.getString("instructor_id"));
        mark.setSemester(rs.getString("semester"));
        mark.setYear(rs.getInt("year"));
        mark.setMarks(rs.getDouble("marks"));
        mark.setLetterGrade(rs.getString("letter_grade"));
        mark.setGpa(rs.getDouble("gpa"));
        mark.setCreatedAt(rs.getTimestamp("created_at"));
        mark.setUpdatedAt(rs.getTimestamp("updated_at"));
        return mark;
    }
} 