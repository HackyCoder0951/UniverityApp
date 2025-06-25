package com.university.app.model;

public class User {
    private String username;
    private String password;
    private String role;
    private boolean requiresPasswordReset;

    public User(String username, String password, String role, boolean requiresPasswordReset) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.requiresPasswordReset = requiresPasswordReset;
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
} 