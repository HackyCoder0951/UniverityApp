package com.university.app.model;

public class Department {
    private String deptName;
    private String building;
    private double budget;

    public Department(String deptName, String building, double budget) {
        this.deptName = deptName;
        this.building = building;
        this.budget = budget;
    }

    // Getters and Setters
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

    @Override
    public String toString() {
        return deptName; // This is important for JComboBox rendering
    }
} 