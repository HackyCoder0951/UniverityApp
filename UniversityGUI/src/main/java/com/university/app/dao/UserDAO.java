package com.university.app.dao;

import com.university.app.db.DatabaseConnector;
import com.university.app.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public User getUserByUsername(String username) {
        String sql = "SELECT u.uid, u.username, u.password, r.name AS role_name, u.requires_password_reset, u.student_id " +
                     "FROM users u JOIN roles r ON u.role = r.id WHERE u.username = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getString("uid"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role_name"),
                        rs.getBoolean("requires_password_reset"),
                        rs.getString("student_id")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<User> getAllUsers(String currentUser) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT u.uid, u.username, u.password, r.name AS role_name, u.requires_password_reset, u.student_id " +
                     "FROM users u JOIN roles r ON u.role = r.id WHERE u.username != ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, currentUser);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                users.add(new User(
                        rs.getString("uid"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role_name"),
                        rs.getBoolean("requires_password_reset"),
                        rs.getString("student_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public void addUser(User user) throws SQLException {
        String sql = "INSERT INTO users (uid, username, password, role, student_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // For student users, use studentId as UID. For others, set to null (or handle as needed)
            String uidToUse = user.getStudentId() != null ? user.getStudentId() : null;
            pstmt.setString(1, uidToUse);
            pstmt.setString(2, user.getUsername());
            pstmt.setString(3, user.getPassword());
            pstmt.setString(4, user.getRole());
            pstmt.setString(5, user.getStudentId());
            pstmt.executeUpdate();
        }
    }

    public List<String> getPermissionsForUser(String username) {
        List<String> permissions = new ArrayList<>();
        String sql = "SELECT table_name FROM permissions WHERE username = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                permissions.add(rs.getString("table_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return permissions;
    }

    public void addPermission(String username, String tableName) throws SQLException {
        String sql = "INSERT INTO permissions (username, table_name) VALUES (?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, tableName);
            pstmt.executeUpdate();
        }
    }

    public void removePermission(String username, String tableName) throws SQLException {
        String sql = "DELETE FROM permissions WHERE username = ? AND table_name = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, tableName);
            pstmt.executeUpdate();
        }
    }

    public void updateUser(String originalUid, User user) throws SQLException {
        String sql = "UPDATE users SET username = ?, password = ?, role = ?, student_id = ? WHERE uid = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getRole());
            pstmt.setString(4, user.getStudentId());
            pstmt.setString(5, originalUid);
            pstmt.executeUpdate();
        }
    }

    public void updatePassword(String username, String newPassword) throws SQLException {
        String sql = "UPDATE users SET password = ? WHERE username = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newPassword);
            pstmt.setString(2, username);
            pstmt.executeUpdate();
        }
    }

    public void deleteUser(String uid) throws SQLException {
        String sql = "DELETE FROM users WHERE uid = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, uid);
            pstmt.executeUpdate();
        }
    }

    public void setRequiresPasswordResetFlag(String username, boolean requiresReset) throws SQLException {
        String sql = "UPDATE users SET requires_password_reset = ? WHERE username = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, requiresReset);
            pstmt.setString(2, username);
            pstmt.executeUpdate();
        }
    }
} 