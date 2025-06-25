package com.university.app.model;

import java.sql.Timestamp;

/**
 * Represents a password reset request made by a user.
 * This model class holds information about the request, including which user
 * made the request and the timestamp of the request.
 */
public class PasswordRequest {
    private int requestId;
    private int userId;
    private String username; // Denormalized for easy display in the UI
    private Timestamp requestDate;

    /**
     * Constructs a new PasswordRequest with specified details.
     *
     * @param requestId The unique identifier for the request.
     * @param userId The ID of the user who made the request.
     * @param username The username of the user (for display purposes).
     * @param requestDate The date and time the request was made.
     */
    public PasswordRequest(int requestId, int userId, String username, Timestamp requestDate) {
        this.requestId = requestId;
        this.userId = userId;
        this.username = username;
        this.requestDate = requestDate;
    }

    // Getters

    /**
     * @return The unique ID of the password reset request.
     */
    public int getRequestId() {
        return requestId;
    }

    /**
     * @return The ID of the user who requested the reset.
     */
    public int getUserId() {
        return userId;
    }

    /**
     * @return The username of the user who requested the reset.
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return The timestamp of when the request was made.
     */
    public Timestamp getRequestDate() {
        return requestDate;
    }
} 