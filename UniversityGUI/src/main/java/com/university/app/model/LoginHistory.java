package com.university.app.model;

import java.sql.Timestamp;

public class LoginHistory {
    private int id;
    private String uid;
    private String username;
    private Timestamp loginTime;
    private Timestamp logoutTime;
    private boolean isActive;

    public LoginHistory() {}
    public LoginHistory(int id, String uid, String username, Timestamp loginTime, Timestamp logoutTime, boolean isActive) {
        this.id = id;
        this.uid = uid;
        this.username = username;
        this.loginTime = loginTime;
        this.logoutTime = logoutTime;
        this.isActive = isActive;
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public Timestamp getLoginTime() { return loginTime; }
    public void setLoginTime(Timestamp loginTime) { this.loginTime = loginTime; }
    public Timestamp getLogoutTime() { return logoutTime; }
    public void setLogoutTime(Timestamp logoutTime) { this.logoutTime = logoutTime; }
    public boolean isIsActive() { return isActive; }
    public void setIsActive(boolean isActive) { this.isActive = isActive; }
} 