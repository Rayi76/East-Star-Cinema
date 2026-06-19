package com.theater.utils;

import com.theater.model.Admin;
import com.theater.model.User;

public class UserSession {

    private static UserSession instance;

    private User currentUser;
    private Admin currentAdmin;
    private boolean isAdmin;

    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void loginUser(User user) {
        this.currentUser = user;
        this.currentAdmin = null;
        this.isAdmin = false;
    }

    public void loginAdmin(Admin admin) {
        this.currentAdmin = admin;
        this.currentUser = null;
        this.isAdmin = true;
    }

    public void logout() {
        this.currentUser = null;
        this.currentAdmin = null;
        this.isAdmin = false;
    }

    public boolean isLoggedIn() {
        return currentUser != null || currentAdmin != null;
    }

    public User getCurrentUser() { return currentUser; }
    public Admin getCurrentAdmin() { return currentAdmin; }
    public boolean isAdmin() { return isAdmin; }
}
