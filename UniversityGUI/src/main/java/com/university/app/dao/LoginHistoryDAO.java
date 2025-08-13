package com.university.app.dao;

import com.university.app.db.DatabaseConnector;
import com.university.app.model.LoginHistory;
import java.sql.*;
import java.util.*;

public class LoginHistoryDAO {
    public void logLogin(String uid, String username) throws SQLException {
        String sql = "INSERT INTO login_history (uid, username) VALUES (?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, uid);
            pstmt.setString(2, username);
            pstmt.executeUpdate();
        }
    }
    public void logLogout(String uid) throws SQLException {
        String sql = "UPDATE login_history SET logout_time = CURRENT_TIMESTAMP, is_active = 0 WHERE uid = ? AND is_active = 1";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, uid);
            pstmt.executeUpdate();
        }
    }
    public List<LoginHistory> getAllHistory() {
        List<LoginHistory> list = new ArrayList<>();
        String sql = "SELECT * FROM login_history ORDER BY login_time DESC";
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                LoginHistory lh = new LoginHistory();
                lh.setId(rs.getInt("id"));
                lh.setUid(rs.getString("uid"));
                lh.setUsername(rs.getString("username"));
                lh.setLoginTime(rs.getTimestamp("login_time"));
                lh.setLogoutTime(rs.getTimestamp("logout_time"));
                lh.setIsActive(rs.getBoolean("is_active"));
                list.add(lh);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    public List<LoginHistory> getActiveLogins() {
        List<LoginHistory> list = new ArrayList<>();
        String sql = "SELECT * FROM login_history WHERE is_active = 1";
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                LoginHistory lh = new LoginHistory();
                lh.setId(rs.getInt("id"));
                lh.setUid(rs.getString("uid"));
                lh.setUsername(rs.getString("username"));
                lh.setLoginTime(rs.getTimestamp("login_time"));
                lh.setLogoutTime(rs.getTimestamp("logout_time"));
                lh.setIsActive(rs.getBoolean("is_active"));
                list.add(lh);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
} 