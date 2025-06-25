package com.university.app.dao;

import com.university.app.db.DatabaseConnector;
import com.university.app.model.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for handling CRUD operations for Student entities.
 * Note: This class was part of the initial static design and has been largely
 * superseded by the dynamic GenericDAO for all CRUD operations.
 */
public class StudentDAO {

    /**
     * Fetches all students from the database.
     * @return A list of all Student objects.
     */
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM student";
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                students.add(new Student(
                        rs.getString("ID"),
                        rs.getString("name"),
                        rs.getString("dept_name"),
                        rs.getInt("tot_cred")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    /**
     * Adds a new student to the database.
     * @param student The Student object to add.
     */
    public void addStudent(Student student) {
        String sql = "INSERT INTO student (ID, name, dept_name, tot_cred) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, student.getId());
            pstmt.setString(2, student.getName());
            pstmt.setString(3, student.getDeptName());
            pstmt.setInt(4, student.getTotCred());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
} 