package com.theater.model;

public interface AdminDAO {
    int login(String username, String password);
    boolean addAdmin(Admin admin);
    Admin findAdminById(int adminID);
}
