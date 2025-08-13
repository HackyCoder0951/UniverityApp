package com.university.app.model;

import java.sql.Timestamp;

public class PasswordRequest {
    private int requestId;
    private String username;
    private Timestamp requestDate;
    private String status;

    public PasswordRequest(int requestId, String username, Timestamp requestDate, String status) {
        this.requestId = requestId;
        this.username = username;
        this.requestDate = requestDate;
        this.status = status;
    }

    // Getters
    public int getRequestId() {
        return requestId;
    }

    public String getUsername() {
        return username;
    }

    public Timestamp getRequestDate() {
        return requestDate;
    }

    public String getStatus() {
        return status;
    }
} 