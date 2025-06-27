package com.university.app.model;

public class User {
    private String uid;
    private String username;
    private String password;
    private String role;
    private boolean requiresPasswordReset;
    private String studentId;

    public User(String uid, String username, String password, String role, boolean requiresPasswordReset, String studentId) {
        this.uid = uid;
        this.username = username;
        this.password = password;
        this.role = role;
        this.requiresPasswordReset = requiresPasswordReset;
        this.studentId = studentId;
    }

    // Getters
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public boolean isRequiresPasswordReset() {
        return requiresPasswordReset;
    }

    public String getUid() {
        return uid;
    }

    public String getStudentId() {
        return studentId;
    }

    @Override
    public String toString() {
        return uid + " - " + username + (studentId != null ? " (Student ID: " + studentId + ")" : "");
    }
} 