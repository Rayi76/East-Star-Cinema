package com.theater.model;

import com.theater.utils.DatabaseHelper;

import java.sql.*;

public class AdminDAOImpl implements AdminDAO {

    @Override
    public int login(String username, String password) {
        String sql = "SELECT admin_id, password FROM admins WHERE username = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String dbPassword = rs.getString("password");
                    if (dbPassword != null && dbPassword.equals(password)) {
                        return rs.getInt("admin_id");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("管理員登入失敗: " + e.getMessage());
        }
        return -1;
    }

    @Override
    public boolean addAdmin(Admin admin) {
        String sql = "INSERT INTO admins (username, password) VALUES (?, ?)";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, admin.getUsername());
            ps.setString(2, admin.getPassword());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("新增管理員失敗: " + e.getMessage());
        }
        return false;
    }

    @Override
    public Admin findAdminById(int adminID) {
        String sql = "SELECT * FROM admins WHERE admin_id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, adminID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Admin admin = new Admin();
                    admin.setAdminID(rs.getInt("admin_id"));
                    admin.setUsername(rs.getString("username"));
                    admin.setPassword(rs.getString("password"));
                    return admin;
                }
            }
        } catch (SQLException e) {
            System.err.println("查詢管理員失敗: " + e.getMessage());
        }
        return null;
    }
}
