package com.university.app.model;

import java.sql.Timestamp;

public class Marks {
    private int markId;
    private String studentId;
    private String courseId;
    private String sectionId;
    private String instructorId;
    private String semester;
    private int year;
    private double marks;
    private String letterGrade;
    private double gpa;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Getters and setters
    public int getMarkId() { return markId; }
    public void setMarkId(int markId) { this.markId = markId; }
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }
    public String getSectionId() { return sectionId; }
    public void setSectionId(String sectionId) { this.sectionId = sectionId; }
    public String getInstructorId() { return instructorId; }
    public void setInstructorId(String instructorId) { this.instructorId = instructorId; }
    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public double getMarks() { return marks; }
    public void setMarks(double marks) { this.marks = marks; }
    public String getLetterGrade() { return letterGrade; }
    public void setLetterGrade(String letterGrade) { this.letterGrade = letterGrade; }
    public double getGpa() { return gpa; }
    public void setGpa(double gpa) { this.gpa = gpa; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    // Constructors
    public Marks() {}

    public Marks(int markId, String studentId, String courseId, String sectionId, String instructorId, String semester, int year, double marks, String letterGrade, double gpa, Timestamp createdAt, Timestamp updatedAt) {
        this.markId = markId;
        this.studentId = studentId;
        this.courseId = courseId;
        this.sectionId = sectionId;
        this.instructorId = instructorId;
        this.semester = semester;
        this.year = year;
        this.marks = marks;
        this.letterGrade = letterGrade;
        this.gpa = gpa;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
} 