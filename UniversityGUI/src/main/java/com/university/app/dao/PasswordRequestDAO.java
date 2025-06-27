package com.university.app.dao;

import com.university.app.db.DatabaseConnector;
import com.university.app.model.PasswordRequest;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PasswordRequestDAO {

    public void createRequest(String username) throws SQLException {
        String sql = "INSERT INTO password_requests (username) VALUES (?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.executeUpdate();
        }
    }

    public List<PasswordRequest> getPendingRequests() {
        List<PasswordRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM password_requests WHERE status = 'pending'";
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                requests.add(new PasswordRequest(
                        rs.getInt("request_id"),
                        rs.getString("username"),
                        rs.getTimestamp("request_date"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }

    public void updateRequestStatus(int requestId, String status) throws SQLException {
        String sql = "UPDATE password_requests SET status = ? WHERE request_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, requestId);
            pstmt.executeUpdate();
        }
    }

    public List<PasswordRequest> getAllRequests() {
        List<PasswordRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM password_requests ORDER BY request_date DESC";
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                requests.add(new PasswordRequest(
                        rs.getInt("request_id"),
                        rs.getString("username"),
                        rs.getTimestamp("request_date"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }
} 