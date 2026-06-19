package com.theater.model;

import com.theater.utils.DatabaseHelper;

import java.sql.*;
import java.time.LocalDate;

public class UserDAOImpl implements UserDAO {

    @Override
    public int login(String username, String password) {
        User user = findUserByUsername(username);
        if (user != null) {
            return user.login(username, password);
        }
        return -1;
    }

    @Override
    public void updateStardust(int userID, int points) {
        String sql = "UPDATE users SET stardust = stardust + ? WHERE user_id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, points);
            ps.setInt(2, userID);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("更新星塵失敗: " + e.getMessage());
        }
    }

    @Override
    public void updateConsumption(int userID, int amount) {
        String sql = "UPDATE users SET consumption = consumption + ? WHERE user_id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, amount);
            ps.setInt(2, userID);
            ps.executeUpdate();
            checkLevel(userID);
        } catch (SQLException e) {
            System.err.println("更新消費失敗: " + e.getMessage());
        }
    }

    @Override
    public void checkLevel(int userID) {
        User user = findUserById(userID);
        if (user == null) return;
        user.checkLevel();
        String sql = "UPDATE users SET level = ? WHERE user_id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getLevel().name());
            ps.setInt(2, userID);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("更新會員等級失敗: " + e.getMessage());
        }
    }

    @Override
    public boolean registerUser(User user) {
        if (findUserByUsername(user.getUsername()) != null) {
            return false;
        }
        String sql = """
                INSERT INTO users (username, password, level, stardust, consumption, created_date, status)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, User.RoleLevel.EARTH.name());
            ps.setInt(4, 0);
            ps.setInt(5, 0);
            ps.setString(6, LocalDate.now().toString());
            ps.setString(7, User.UserStatus.ACTIVE.name());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("註冊失敗: " + e.getMessage());
        }
        return false;
    }

    @Override
    public User findUserById(int userID) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return extractUser(rs);
            }
        } catch (SQLException e) {
            System.err.println("查詢會員失敗: " + e.getMessage());
        }
        return null;
    }

    @Override
    public User findUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return extractUser(rs);
            }
        } catch (SQLException e) {
            System.err.println("查詢會員失敗: " + e.getMessage());
        }
        return null;
    }

    private User extractUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserID(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setLevel(User.RoleLevel.valueOf(rs.getString("level")));
        user.setStardust(rs.getInt("stardust"));
        user.setConsumption(rs.getInt("consumption"));
        user.setCreatedDate(LocalDate.parse(rs.getString("created_date")));
        user.setStatus(User.UserStatus.valueOf(rs.getString("status")));
        return user;
    }
}
