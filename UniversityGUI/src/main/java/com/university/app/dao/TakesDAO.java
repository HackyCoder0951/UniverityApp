package com.university.app.dao;

import com.university.app.db.DatabaseConnector;
import com.university.app.model.Takes;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TakesDAO {

    public List<Takes> getAllTakes() {
        List<Takes> takesList = new ArrayList<>();
        String sql = "SELECT * FROM takes";

        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                takesList.add(new Takes(
                        rs.getString("ID"),
                        rs.getString("course_id"),
                        rs.getInt("sec_id"),
                        rs.getString("semester"),
                        rs.getInt("year"),
                        rs.getString("grade")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return takesList;
    }

    public void addTakes(Takes takes) throws SQLException {
        String sql = "INSERT INTO takes (ID, course_id, sec_id, semester, year, grade) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, takes.getStudentId());
            pstmt.setString(2, takes.getCourseId());
            pstmt.setInt(3, takes.getSecId());
            pstmt.setString(4, takes.getSemester());
            pstmt.setInt(5, takes.getYear());
            pstmt.setString(6, takes.getGrade());
            pstmt.executeUpdate();
        }
    }

    public void updateTakes(Takes takes) throws SQLException {
        String sql = "UPDATE takes SET grade = ? WHERE ID = ? AND course_id = ? AND sec_id = ? AND semester = ? AND year = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, takes.getGrade());
            pstmt.setString(2, takes.getStudentId());
            pstmt.setString(3, takes.getCourseId());
            pstmt.setInt(4, takes.getSecId());
            pstmt.setString(5, takes.getSemester());
            pstmt.setInt(6, takes.getYear());
            pstmt.executeUpdate();
        }
    }

    public void deleteTakes(String studentId, String courseId, int secId, String semester, int year) throws SQLException {
        String sql = "DELETE FROM takes WHERE ID = ? AND course_id = ? AND sec_id = ? AND semester = ? AND year = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            pstmt.setString(2, courseId);
            pstmt.setInt(3, secId);
            pstmt.setString(4, semester);
            pstmt.setInt(5, year);
            pstmt.executeUpdate();
        }
    }

    public List<Takes> getTakesForStudent(String studentId) {
        List<Takes> takesList = new ArrayList<>();
        String sql = "SELECT * FROM takes WHERE ID = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    takesList.add(new Takes(
                        rs.getString("ID"),
                        rs.getString("course_id"),
                        rs.getInt("sec_id"),
                        rs.getString("semester"),
                        rs.getInt("year"),
                        rs.getString("grade")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return takesList;
    }
} 