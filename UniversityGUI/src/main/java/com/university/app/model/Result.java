package com.university.app.model;

import java.sql.Timestamp;

public class Result {
    private int resultId;
    private String studentId;
    private String semester;
    private int year;
    private Double sgpa;
    private Double cgpa;
    private Integer totalCredits;
    private String resultType; // 'semester', 'annual', 'final'
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Getters and setters
    public int getResultId() { return resultId; }
    public void setResultId(int resultId) { this.resultId = resultId; }
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public Double getSgpa() { return sgpa; }
    public void setSgpa(Double sgpa) { this.sgpa = sgpa; }
    public Double getCgpa() { return cgpa; }
    public void setCgpa(Double cgpa) { this.cgpa = cgpa; }
    public Integer getTotalCredits() { return totalCredits; }
    public void setTotalCredits(Integer totalCredits) { this.totalCredits = totalCredits; }
    public String getResultType() { return resultType; }
    public void setResultType(String resultType) { this.resultType = resultType; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    // Constructors
    public Result() {}

    public Result(int resultId, String studentId, String semester, int year, Double sgpa, Double cgpa, Integer totalCredits, String resultType, Timestamp createdAt, Timestamp updatedAt) {
        this.resultId = resultId;
        this.studentId = studentId;
        this.semester = semester;
        this.year = year;
        this.sgpa = sgpa;
        this.cgpa = cgpa;
        this.totalCredits = totalCredits;
        this.resultType = resultType;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
} 