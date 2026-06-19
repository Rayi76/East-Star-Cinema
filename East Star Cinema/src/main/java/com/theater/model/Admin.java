package com.theater.model;

public class Admin {

    private int adminID;
    private String username;
    private String password;

    public Admin() {}

    /** 新增管理員帳號時建構 Admin 物件 */
    public Admin(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /** 登入：回傳 ID，失敗回傳 -1 */
    public int login(String username, String password) {
        if (this.username != null && this.username.equals(username)
                && this.password != null && this.password.equals(password)) {
            return adminID;
        }
        return -1;
    }

    public int getAdminID() { return adminID; }
    public void setAdminID(int adminID) { this.adminID = adminID; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
