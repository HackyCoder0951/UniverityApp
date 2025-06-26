package com.university.app.model;

public class Prereq {
    private String courseId;
    private String prereqId;

    public Prereq(String courseId, String prereqId) {
        this.courseId = courseId;
        this.prereqId = prereqId;
    }

    // Getters and Setters
    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getPrereqId() {
        return prereqId;
    }

    public void setPrereqId(String prereqId) {
        this.prereqId = prereqId;
    }
} 