package com.university.app.service;

import com.university.app.model.User;

/**
 * Manages the current user's session throughout the application's lifecycle.
 * This class follows the Singleton design pattern to ensure that there is only
 * one instance of the user session, providing a global point of access to the
 * currently logged-in user's data. It also holds permissions for the current user.
 */
public class UserSession {
    private static UserSession instance;
    private User currentUser;
    private java.util.List<String> userPermissions;

    /**
     * Private constructor to prevent instantiation from outside the class.
     */
    private UserSession() {}

    /**
     * Provides the global access point to the UserSession instance.
     * Creates the instance if it doesn't exist yet (lazy initialization).
     *
     * @return The single instance of UserSession.
     */
    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    /**
     * @return The User object for the currently logged-in user.
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Sets the user for the current session upon successful login.
     * @param currentUser The user who has logged in.
     */
    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    /**
     * @return A list of table names the current user has permissions for.
     */
    public java.util.List<String> getUserPermissions() {
        return userPermissions;
    }

    /**
     * Sets the permissions for the current user.
     * @param userPermissions A list of table names.
     */
    public void setUserPermissions(java.util.List<String> userPermissions) {
        this.userPermissions = userPermissions;
    }

    /**
     * Clears the current session data. This is typically called on logout.
     */
    public void clearSession() {
        this.currentUser = null;
        this.userPermissions = null;
    }

    public boolean isAdmin() {
        return currentUser != null && "admin".equals(currentUser.getRole());
    }
} 