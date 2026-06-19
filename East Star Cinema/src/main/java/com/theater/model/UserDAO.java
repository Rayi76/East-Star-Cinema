package com.theater.model;

import java.util.List;

public interface UserDAO {
    int login(String username, String password);
    void updateStardust(int userID, int points);
    void updateConsumption(int userID, int amount);
    void checkLevel(int userID);
    boolean registerUser(User user);
    User findUserById(int userID);
    User findUserByUsername(String username);
}
