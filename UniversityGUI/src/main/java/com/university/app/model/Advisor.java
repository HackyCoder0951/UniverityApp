package com.university.app.model;

public class Advisor {
    private String studentId;
    private String instructorId;

    public Advisor(String studentId, String instructorId) {
        this.studentId = studentId;
        this.instructorId = instructorId;
    }

    // Getters and Setters
    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(String instructorId) {
        this.instructorId = instructorId;
    }
} 