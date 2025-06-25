package com.university.app.dao;

import com.university.app.db.DatabaseConnector;
import com.university.app.model.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM student";

        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String id = rs.getString("ID");
                String name = rs.getString("name");
                String deptName = rs.getString("dept_name");
                int totalCredits = rs.getInt("tot_cred");
                students.add(new Student(id, name, deptName, totalCredits));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    public void addStudent(Student student) throws SQLException {
        String sql = "INSERT INTO student (ID, name, dept_name, tot_cred) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, student.getId());
            pstmt.setString(2, student.getName());
            pstmt.setString(3, student.getDeptName());
            pstmt.setInt(4, student.getTotalCredits());
            pstmt.executeUpdate();
        }
    }
} 