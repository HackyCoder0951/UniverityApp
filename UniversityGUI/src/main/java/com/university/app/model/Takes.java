package com.university.app.model;

public class Takes {
    private String studentId;
    private String courseId;
    private int secId;
    private String semester;
    private int year;
    private String grade;

    public Takes(String studentId, String courseId, int secId, String semester, int year, String grade) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.secId = secId;
        this.semester = semester;
        this.year = year;
        this.grade = grade;
    }

    // Getters and Setters
    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
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

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }
} 