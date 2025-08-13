package com.university.app.dao;

import com.university.app.db.DatabaseConnector;
import com.university.app.model.Prereq;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrereqDAO {

    public List<Prereq> getAllPrereqs() {
        List<Prereq> prereqs = new ArrayList<>();
        String sql = "SELECT * FROM prereq";

        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                prereqs.add(new Prereq(
                        rs.getString("course_id"),
                        rs.getString("prereq_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return prereqs;
    }

    public void addPrereq(Prereq prereq) throws SQLException {
        String sql = "INSERT INTO prereq (course_id, prereq_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, prereq.getCourseId());
            pstmt.setString(2, prereq.getPrereqId());
            pstmt.executeUpdate();
        }
    }

    public void updatePrereq(Prereq prereq) throws SQLException {
        String sql = "UPDATE prereq SET prereq_id = ? WHERE course_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, prereq.getPrereqId());
            pstmt.setString(2, prereq.getCourseId());
            pstmt.executeUpdate();
        }
    }

    public void deletePrereq(String courseId, String prereqId) throws SQLException {
        String sql = "DELETE FROM prereq WHERE course_id = ? AND prereq_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, courseId);
            pstmt.setString(2, prereqId);
            pstmt.executeUpdate();
        }
    }
} 