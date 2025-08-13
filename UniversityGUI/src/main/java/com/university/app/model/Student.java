package com.university.app.model;

public class Student {
    private String id;
    private String name;
    private String deptName;
    private int totalCredits;

    public Student(String id, String name, String deptName, int totalCredits) {
        this.id = id;
        this.name = name;
        this.deptName = deptName;
        this.totalCredits = totalCredits;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public int getTotalCredits() {
        return totalCredits;
    }

    public void setTotalCredits(int totalCredits) {
        this.totalCredits = totalCredits;
    }
} 