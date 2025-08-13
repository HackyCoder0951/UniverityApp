package com.university.app.dao;

import com.university.app.db.DatabaseConnector;
import com.university.app.model.Classroom;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClassroomDAO {

    public List<Classroom> getAllClassrooms() {
        List<Classroom> classrooms = new ArrayList<>();
        String sql = "SELECT * FROM classroom";

        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                classrooms.add(new Classroom(
                        rs.getString("building"),
                        rs.getInt("room_number"),
                        rs.getInt("capacity")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return classrooms;
    }

    public void addClassroom(Classroom classroom) throws SQLException {
        String sql = "INSERT INTO classroom (building, room_number, capacity) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, classroom.getBuilding());
            pstmt.setInt(2, classroom.getRoomNumber());
            pstmt.setInt(3, classroom.getCapacity());
            pstmt.executeUpdate();
        }
    }

    public void updateClassroom(Classroom classroom) throws SQLException {
        String sql = "UPDATE classroom SET capacity = ? WHERE building = ? AND room_number = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, classroom.getCapacity());
            pstmt.setString(2, classroom.getBuilding());
            pstmt.setInt(3, classroom.getRoomNumber());
            pstmt.executeUpdate();
        }
    }

    public void deleteClassroom(String building, int roomNumber) throws SQLException {
        String sql = "DELETE FROM classroom WHERE building = ? AND room_number = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, building);
            pstmt.setInt(2, roomNumber);
            pstmt.executeUpdate();
        }
    }
} 