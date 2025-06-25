package com.university.app.model;

/**
 * Represents a student in the university.
 * This model class holds the student's ID, name, department name, and total credits.
 * Note: This class was part of the initial static design and is not directly
 * used by the final dynamic CRUD functionality, but is kept for reference.
 */
public class Student {
    private String id;
    private String name;
    private String deptName;
    private int totCred;

    /**
     * Constructs a new Student with specified details.
     *
     * @param id The student's unique ID.
     * @param name The student's full name.
     * @param deptName The name of the student's department.
     * @param totCred The total credits earned by the student.
     */
    public Student(String id, String name, String deptName, int totCred) {
        this.id = id;
        this.name = name;
        this.deptName = deptName;
        this.totCred = totCred;
    }

    // Getters and Setters

    /**
     * @return The student's ID.
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return The student's name.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The name of the student's department.
     */
    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    /**
     * @return The total credits of the student.
     */
    public int getTotCred() {
        return totCred;
    }

    public void setTotCred(int totCred) {
        this.totCred = totCred;
    }
} 