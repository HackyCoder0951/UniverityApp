package com.university.app.model;

/**
 * Represents a department in the university.
 * This model class is primarily used to populate dropdown menus (JComboBox)
 * to ensure data consistency when creating or updating records that
 * reference a department.
 */
public class Department {
    private String deptName;
    private String building;
    private double budget;

    /**
     * Constructs a new Department with a specified name.
     *
     * @param deptName The name of the department.
     */
    public Department(String deptName) {
        this.deptName = deptName;
    }

    // Getters and Setters
    /**
     * @return The department's name.
     */
    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    /**
     * Overridden to provide a user-friendly string representation,
     * which is essential for display in JComboBox components.
     *
     * @return The name of the department.
     */
    @Override
    public String toString() {
        return deptName; // This is important for JComboBox rendering
    }
} 