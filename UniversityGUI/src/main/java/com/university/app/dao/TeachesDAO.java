package com.university.app.dao;

import com.university.app.db.DatabaseConnector;
import com.university.app.model.Teaches;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TeachesDAO {

    public List<Teaches> getAllTeaches() {
        List<Teaches> teachesList = new ArrayList<>();
        String sql = "SELECT * FROM teaches";

        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                teachesList.add(new Teaches(
                        rs.getString("ID"),
                        rs.getString("course_id"),
                        rs.getInt("sec_id"),
                        rs.getString("semester"),
                        rs.getInt("year")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return teachesList;
    }

    public void addTeaches(Teaches teaches) throws SQLException {
        String sql = "INSERT INTO teaches (ID, course_id, sec_id, semester, year) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, teaches.getInstructorId());
            pstmt.setString(2, teaches.getCourseId());
            pstmt.setInt(3, teaches.getSecId());
            pstmt.setString(4, teaches.getSemester());
            pstmt.setInt(5, teaches.getYear());
            pstmt.executeUpdate();
        }
    }

    public void updateTeaches(Teaches teaches) throws SQLException {
        String sql = "UPDATE teaches SET course_id = ?, sec_id = ?, semester = ?, year = ? WHERE ID = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, teaches.getCourseId());
            pstmt.setInt(2, teaches.getSecId());
            pstmt.setString(3, teaches.getSemester());
            pstmt.setInt(4, teaches.getYear());
            pstmt.setString(5, teaches.getInstructorId());
            pstmt.executeUpdate();
        }
    }

    public void deleteTeaches(String instructorId, String courseId, int secId, String semester, int year) throws SQLException {
        String sql = "DELETE FROM teaches WHERE ID = ? AND course_id = ? AND sec_id = ? AND semester = ? AND year = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, instructorId);
            pstmt.setString(2, courseId);
            pstmt.setInt(3, secId);
            pstmt.setString(4, semester);
            pstmt.setInt(5, year);
            pstmt.executeUpdate();
        }
    }
} 