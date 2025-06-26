package com.university.app.dao;

import com.university.app.db.DatabaseConnector;
import com.university.app.model.Advisor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdvisorDAO {

    public List<Advisor> getAllAdvisors() {
        List<Advisor> advisors = new ArrayList<>();
        String sql = "SELECT * FROM advisor";

        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                advisors.add(new Advisor(
                        rs.getString("sID"),
                        rs.getString("iID")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return advisors;
    }

    public void addAdvisor(Advisor advisor) throws SQLException {
        String sql = "INSERT INTO advisor (sID, iID) VALUES (?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, advisor.getStudentId());
            pstmt.setString(2, advisor.getInstructorId());
            pstmt.executeUpdate();
        }
    }

    public void updateAdvisor(Advisor advisor) throws SQLException {
        String sql = "UPDATE advisor SET iID = ? WHERE sID = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, advisor.getInstructorId());
            pstmt.setString(2, advisor.getStudentId());
            pstmt.executeUpdate();
        }
    }

    public void deleteAdvisor(String studentId, String instructorId) throws SQLException {
        String sql = "DELETE FROM advisor WHERE sID = ? AND iID = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            pstmt.setString(2, instructorId);
            pstmt.executeUpdate();
        }
    }
} 