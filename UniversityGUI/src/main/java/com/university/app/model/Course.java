package com.university.app.model;

/**
 * Represents a course in the university.
 * This model class holds the course ID, title, department name, and credits.
 * Note: This class was part of the initial static design and is not directly
 * used by the final dynamic CRUD functionality, but is kept for reference.
 */
public class Course {
    private String courseId;
    private String title;
    private String deptName;
    private int credits;

    /**
     * Constructs a new Course with specified details.
     *
     * @param courseId The unique ID for the course.
     * @param title The title of the course.
     * @param deptName The name of the department offering the course.
     * @param credits The number of credits the course is worth.
     */
    public Course(String courseId, String title, String deptName, int credits) {
        this.courseId = courseId;
        this.title = title;
        this.deptName = deptName;
        this.credits = credits;
    }

    // Getters and Setters

    /**
     * @return The course ID.
     */
    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    /**
     * @return The course title.
     */
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return The name of the department offering the course.
     */
    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    /**
     * @return The number of credits for the course.
     */
    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }
} 