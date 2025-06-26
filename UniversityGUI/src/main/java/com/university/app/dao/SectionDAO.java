package com.university.app.dao;

import com.university.app.db.DatabaseConnector;
import com.university.app.model.Section;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SectionDAO {

    public List<Section> getAllSections() {
        List<Section> sections = new ArrayList<>();
        String sql = "SELECT * FROM section";

        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                sections.add(new Section(
                        rs.getString("course_id"),
                        rs.getInt("sec_id"),
                        rs.getString("semester"),
                        rs.getInt("year"),
                        rs.getString("building"),
                        rs.getInt("room_number"),
                        rs.getInt("time_slot_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sections;
    }

    public void addSection(Section section) throws SQLException {
        String sql = "INSERT INTO section (course_id, sec_id, semester, year, building, room_number, time_slot_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, section.getCourseId());
            pstmt.setInt(2, section.getSecId());
            pstmt.setString(3, section.getSemester());
            pstmt.setInt(4, section.getYear());
            pstmt.setString(5, section.getBuilding());
            pstmt.setInt(6, section.getRoomNumber());
            pstmt.setInt(7, section.getTimeSlotId());
            pstmt.executeUpdate();
        }
    }

    public void updateSection(Section section) throws SQLException {
        String sql = "UPDATE section SET building = ?, room_number = ?, time_slot_id = ? WHERE course_id = ? AND sec_id = ? AND semester = ? AND year = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, section.getBuilding());
            pstmt.setInt(2, section.getRoomNumber());
            pstmt.setInt(3, section.getTimeSlotId());
            pstmt.setString(4, section.getCourseId());
            pstmt.setInt(5, section.getSecId());
            pstmt.setString(6, section.getSemester());
            pstmt.setInt(7, section.getYear());
            pstmt.executeUpdate();
        }
    }

    public void deleteSection(String courseId, int secId, String semester, int year) throws SQLException {
        String sql = "DELETE FROM section WHERE course_id = ? AND sec_id = ? AND semester = ? AND year = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, courseId);
            pstmt.setInt(2, secId);
            pstmt.setString(3, semester);
            pstmt.setInt(4, year);
            pstmt.executeUpdate();
        }
    }
} 