package com.university.app.dao;

import com.university.app.db.DatabaseConnector;
import com.university.app.model.TimeSlot;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TimeSlotDAO {

    public List<TimeSlot> getAllTimeSlots() {
        List<TimeSlot> timeSlots = new ArrayList<>();
        String sql = "SELECT * FROM time_slot";

        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                timeSlots.add(new TimeSlot(
                        rs.getInt("time_slot_id"),
                        rs.getString("day"),
                        rs.getTime("start_time"),
                        rs.getTime("end_time")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return timeSlots;
    }

    public void addTimeSlot(TimeSlot timeSlot) throws SQLException {
        String sql = "INSERT INTO time_slot (time_slot_id, day, start_time, end_time) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, timeSlot.getTimeSlotId());
            pstmt.setString(2, timeSlot.getDay());
            pstmt.setTime(3, timeSlot.getStartTime());
            pstmt.setTime(4, timeSlot.getEndTime());
            pstmt.executeUpdate();
        }
    }

    public void updateTimeSlot(TimeSlot timeSlot) throws SQLException {
        String sql = "UPDATE time_slot SET day = ?, start_time = ?, end_time = ? WHERE time_slot_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, timeSlot.getDay());
            pstmt.setTime(2, timeSlot.getStartTime());
            pstmt.setTime(3, timeSlot.getEndTime());
            pstmt.setInt(4, timeSlot.getTimeSlotId());
            pstmt.executeUpdate();
        }
    }

    public void deleteTimeSlot(int timeSlotId) throws SQLException {
        String sql = "DELETE FROM time_slot WHERE time_slot_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, timeSlotId);
            pstmt.executeUpdate();
        }
    }
} 