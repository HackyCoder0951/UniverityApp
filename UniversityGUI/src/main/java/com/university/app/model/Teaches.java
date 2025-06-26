package com.university.app.model;

public class Teaches {
    private String instructorId;
    private String courseId;
    private int secId;
    private String semester;
    private int year;

    public Teaches(String instructorId, String courseId, int secId, String semester, int year) {
        this.instructorId = instructorId;
        this.courseId = courseId;
        this.secId = secId;
        this.semester = semester;
        this.year = year;
    }

    // Getters and Setters
    public String getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(String instructorId) {
        this.instructorId = instructorId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public int getSecId() {
        return secId;
    }

    public void setSecId(int secId) {
        this.secId = secId;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
} 