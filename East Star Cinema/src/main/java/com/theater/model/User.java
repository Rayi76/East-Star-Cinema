package com.theater.model;

import java.time.LocalDate;

public class User {

    public enum UserStatus { ACTIVE, SUSPENDED }

    public enum RoleLevel { EARTH, STAR, GALAXY, UNIVERSE }

    private int userID;
    private String username;
    private String password;
    private RoleLevel level;
    private int stardust;
    private int consumption;
    private LocalDate createdDate;
    private UserStatus status;

    public User() {
        this.level = RoleLevel.EARTH;
        this.stardust = 0;
        this.consumption = 0;
        this.createdDate = LocalDate.now();
        this.status = UserStatus.ACTIVE;
    }

    /** 註冊帳號時建構新 User 物件 */
    public User(String username, String password) {
        this();
        this.username = username;
        this.password = password;
    }

    private void updateStardust(int points) {
        this.stardust += points;
    }

    private void updateConsumption(int amount) {
        this.consumption += amount;
    }

    /** 每個月底檢查帳號活躍程度與會員等級 */
    public void checkLevel() {
        if (consumption >= 10000) {
            level = RoleLevel.UNIVERSE;
        } else if (consumption >= 5000) {
            level = RoleLevel.GALAXY;
        } else if (consumption >= 2000) {
            level = RoleLevel.STAR;
        } else {
            level = RoleLevel.EARTH;
        }
    }

    /** 登入：回傳 ID，失敗回傳 -1 */
    public int login(String username, String password) {
        if (this.username != null && this.username.equals(username)
                && this.password != null && this.password.equals(password)
                && status == UserStatus.ACTIVE) {
            return userID;
        }
        return -1;
    }

    public void applyStardustChange(int points) {
        updateStardust(points);
    }

    public void applyConsumption(int amount) {
        updateConsumption(amount);
        checkLevel();
    }

    public int getUserID() { return userID; }
    public void setUserID(int userID) { this.userID = userID; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public RoleLevel getLevel() { return level; }
    public void setLevel(RoleLevel level) { this.level = level; }

    public int getStardust() { return stardust; }
    public void setStardust(int stardust) { this.stardust = stardust; }

    public int getConsumption() { return consumption; }
    public void setConsumption(int consumption) { this.consumption = consumption; }

    public LocalDate getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDate createdDate) { this.createdDate = createdDate; }

    public UserStatus getStatus() { return status; }
    public void setStatus(UserStatus status) { this.status = status; }
}
