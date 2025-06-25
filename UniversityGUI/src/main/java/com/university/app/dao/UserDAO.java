package com.university.app.dao;

import com.university.app.db.DatabaseConnector;
import com.university.app.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for handling all CRUD operations for User entities.
 * This class provides methods to interact with the 'users' table in the database.
 */
public class UserDAO {

    /**
     * Finds a user by their username.
     *
     * @param username The username to search for.
     * @return A User object if found, otherwise null.
     */
    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getBoolean("requires_password_reset")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves a list of all users from the database.
     *
     * @return A list of all User objects.
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getBoolean("requires_password_reset")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    /**
     * Adds a new user to the database.
     *
     * @param user The User object containing the new user's data.
     */
    public void addUser(User user) {
        String sql = "INSERT INTO users (username, password, role, requires_password_reset) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getRole());
            pstmt.setBoolean(4, user.isRequiresPasswordReset());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the permissions for a specific user.
     * This is used for 'entry' and 'reporting' roles to determine which tables they can access.
     *
     * @param userId The ID of the user.
     * @return A list of table names the user has permission to access.
     */
    public List<String> getUserPermissions(int userId) {
        List<String> permissions = new ArrayList<>();
        String sql = "SELECT table_name FROM permissions WHERE user_id = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    permissions.add(rs.getString("table_name"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return permissions;
    }

    /**
     * Updates an existing user's information in the database.
     *
     * @param user The User object with updated data.
     */
    public void updateUser(User user) {
        String sql = "UPDATE users SET username = ?, password = ?, role = ?, requires_password_reset = ? WHERE id = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getRole());
            pstmt.setBoolean(4, user.isRequiresPasswordReset());
            pstmt.setInt(5, user.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates a user's password.
     *
     * @param userId The ID of the user whose password is to be updated.
     * @param newPassword The new password.
     */
    public void updatePassword(int userId, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE id = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newPassword);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes a user from the database.
     *
     * @param userId The ID of the user to delete.
     */
    public void deleteUser(int userId) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        }
    }

    /**
     * Sets the 'requires_password_reset' flag for a user.
     *
     * @param userId The ID of the user.
     * @param requiresReset The boolean value to set the flag to.
     */
    public void setRequiresPasswordReset(int userId, boolean requiresReset) {
        String sql = "UPDATE users SET requires_password_reset = ? WHERE id = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, requiresReset);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the permissions for a user.
     * It first deletes all existing permissions and then inserts the new ones.
     *
     * @param userId The ID of the user whose permissions are to be updated.
     * @param permissions A list of table names to grant permission for.
     */
    public void updateUserPermissions(int userId, List<String> permissions) {
        String deleteSql = "DELETE FROM permissions WHERE user_id = ?";
        String insertSql = "INSERT INTO permissions (user_id, table_name) VALUES (?, ?)";

        try (Connection conn = DatabaseConnector.getConnection()) {
            // Transaction to ensure atomicity
            conn.setAutoCommit(false);

            // Delete old permissions
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                deleteStmt.setInt(1, userId);
                deleteStmt.executeUpdate();
            }

            // Insert new permissions
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                for (String tableName : permissions) {
                    insertStmt.setInt(1, userId);
                    insertStmt.setString(2, tableName);
                    insertStmt.addBatch();
                }
                insertStmt.executeBatch();
            }

            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
} 