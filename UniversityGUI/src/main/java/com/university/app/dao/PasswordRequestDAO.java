package com.university.app.dao;

import com.university.app.db.DatabaseConnector;
import com.university.app.model.PasswordRequest;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for handling password reset requests.
 * This class interacts with the 'password_requests' table to create, retrieve,
 * and delete password reset requests.
 */
public class PasswordRequestDAO {

    /**
     * Creates a new password reset request for a given user.
     *
     * @param userId The ID of the user requesting the password reset.
     * @throws SQLException if a database access error occurs.
     */
    public void createRequest(int userId) throws SQLException {
        String sql = "INSERT INTO password_requests (user_id) VALUES (?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        }
    }

    /**
     * Retrieves all pending password reset requests.
     * It joins with the 'users' table to fetch the username for display purposes.
     *
     * @return A list of PasswordRequest objects.
     * @throws SQLException if a database access error occurs.
     */
    public List<PasswordRequest> getAllRequests() throws SQLException {
        List<PasswordRequest> requests = new ArrayList<>();
        String sql = "SELECT pr.request_id, pr.user_id, u.username, pr.request_date " +
                     "FROM password_requests pr " +
                     "JOIN users u ON pr.user_id = u.id " +
                     "ORDER BY pr.request_date ASC";

        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                requests.add(new PasswordRequest(
                        rs.getInt("request_id"),
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getTimestamp("request_date")
                ));
            }
        }
        return requests;
    }

    /**
     * Deletes a password reset request, typically after it has been handled.
     *
     * @param requestId The ID of the request to delete.
     * @throws SQLException if a database access error occurs.
     */
    public void deleteRequest(int requestId) throws SQLException {
        String sql = "DELETE FROM password_requests WHERE request_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, requestId);
            pstmt.executeUpdate();
        }
    }
} 