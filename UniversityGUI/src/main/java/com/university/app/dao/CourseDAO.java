package com.university.app.dao;

import com.university.app.db.DatabaseConnector;
import com.university.app.model.Course;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for handling CRUD operations for Course entities.
 * Note: This class was part of the initial static design and has been largely
 * superseded by the dynamic GenericDAO for all CRUD operations.
 */
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

    /**
     * Adds a new course to the database.
     * @param course The Course object to add.
     */
    public void addCourse(Course course) {
        String sql = "INSERT INTO course (course_id, title, dept_name, credits) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, course.getCourseId());
            pstmt.setString(2, course.getTitle());
            pstmt.setString(3, course.getDeptName());
            pstmt.setInt(4, course.getCredits());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
} 