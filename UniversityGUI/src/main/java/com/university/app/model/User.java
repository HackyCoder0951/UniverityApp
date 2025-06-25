package com.university.app.model;

/**
 * Represents a user of the application.
 * This model class holds information about a user, including their credentials,
 * role, and whether their password needs to be reset.
 */
public class User {

    private int id;
    private String username;
    private String password; // Note: In a real app, this should be a securely hashed password.
    private String role; // e.g., "admin", "entry", "reporting"
    private boolean requiresPasswordReset;

    /**
     * Default constructor.
     */
    public User() {}

    /**
     * Constructs a new User with specified details.
     *
     * @param id The unique identifier for the user.
     * @param username The user's login name.
     * @param password The user's password.
     * @param role The user's role, which determines their permissions.
     * @param requiresPasswordReset A flag indicating if the user must change their password on next login.
     */
    public User(int id, String username, String password, String role, boolean requiresPasswordReset) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.requiresPasswordReset = requiresPasswordReset;
    }

    // Getters and Setters

    /**
     * @return The user's unique ID.
     */
    public int getId() {
        return id;
    }

    /**
     * @param id The user's unique ID.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return The user's username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username The user's username.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return The user's password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password The user's password.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return The user's role (e.g., "admin").
     */
    public String getRole() {
        return role;
    }

    /**
     * @param role The user's role.
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * @return true if the user must reset their password, false otherwise.
     */
    public boolean isRequiresPasswordReset() {
        return requiresPasswordReset;
    }

    /**
     * @param requiresPasswordReset Set to true to force a password reset on next login.
     */
    public void setRequiresPasswordReset(boolean requiresPasswordReset) {
        this.requiresPasswordReset = requiresPasswordReset;
    }
} 