package com.university.app.model;

public class Course {
    private String courseId;
    private String title;
    private String deptName;
    private int credits;

    public Course(String courseId, String title, String deptName, int credits) {
        this.courseId = courseId;
        this.title = title;
        this.deptName = deptName;
        this.credits = credits;
    }

    // Getters and Setters
    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }
} 