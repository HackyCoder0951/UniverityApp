package com.university.app.dao;

import com.university.app.db.DatabaseConnector;
import com.university.app.model.Instructor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for handling CRUD operations for Instructor entities.
 * Note: This class was part of the initial static design and has been largely
 * superseded by the dynamic GenericDAO for all CRUD operations.
 */
public class InstructorDAO {

    public List<Instructor> getAllInstructors() {
        List<Instructor> instructors = new ArrayList<>();
        String sql = "SELECT * FROM instructor";

        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                instructors.add(new Instructor(
                        rs.getString("ID"),
                        rs.getString("name"),
                        rs.getString("dept_name"),
                        rs.getDouble("salary")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return instructors;
    }

    /**
     * Adds a new instructor to the database.
     * @param instructor The Instructor object to add.
     */
    public void addInstructor(Instructor instructor) {
        String sql = "INSERT INTO instructor (ID, name, dept_name, salary) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, instructor.getId());
            pstmt.setString(2, instructor.getName());
            pstmt.setString(3, instructor.getDeptName());
            pstmt.setDouble(4, instructor.getSalary());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
