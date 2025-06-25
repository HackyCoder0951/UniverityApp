package com.university.app.model;

/**
 * Represents an instructor in the university.
 * This model class holds the instructor's ID, name, department name, and salary.
 * Note: This class was part of the initial static design and is not directly
 * used by the final dynamic CRUD functionality, but is kept for reference.
 */
public class Instructor {
    private String id;
    private String name;
    private String deptName;
    private double salary;

    /**
     * Constructs a new Instructor with specified details.
     *
     * @param id The instructor's unique ID.
     * @param name The instructor's full name.
     * @param deptName The name of the instructor's department.
     * @param salary The instructor's salary.
     */
    public Instructor(String id, String name, String deptName, double salary) {
        this.id = id;
        this.name = name;
        this.deptName = deptName;
        this.salary = salary;
    }

    // Getters and Setters

    /**
     * @return The instructor's ID.
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return The instructor's name.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The name of the instructor's department.
     */
    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    /**
     * @return The instructor's salary.
     */
    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }
} 