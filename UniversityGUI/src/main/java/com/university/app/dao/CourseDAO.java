package com.university.app.dao;

import com.university.app.db.DatabaseConnector;
import com.university.app.model.Course;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {

    public List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM course";

        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                courses.add(new Course(
                        rs.getString("course_id"),
                        rs.getString("title"),
                        rs.getString("dept_name"),
                        rs.getInt("credits")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }

    public void addCourse(Course course) throws SQLException {
        String sql = "INSERT INTO course (course_id, title, dept_name, credits) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, course.getCourseId());
            pstmt.setString(2, course.getTitle());
            pstmt.setString(3, course.getDeptName());
            pstmt.setInt(4, course.getCredits());
            pstmt.executeUpdate();
        }
    }
} 