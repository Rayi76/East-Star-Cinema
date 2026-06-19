package com.theater.model;

import com.theater.utils.DatabaseHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CinemaDAOImpl implements CinemaDAO {

    @Override
    public List<String> listCinema() {
        List<String> names = new ArrayList<>();
        String sql = "SELECT cinema_name FROM cinema ORDER BY cinema_id";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) names.add(rs.getString("cinema_name"));
        } catch (SQLException e) {
            System.err.println("查詢影院列表失敗: " + e.getMessage());
        }
        return names;
    }

    @Override
    public boolean insertCinema(Cinema cinema) {
        String sql = "INSERT INTO cinema (cinema_name, info) VALUES (?, ?)";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, cinema.getCinemaName());
            ps.setString(2, cinema.getInfo());
            if (ps.executeUpdate() > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        cinema.setCinemaID(keys.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("新增影院失敗: " + e.getMessage());
        }
        return false;
    }

    @Override
    public Cinema findCinemaById(int cinemaID) {
        String sql = "SELECT * FROM cinema WHERE cinema_id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cinemaID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Cinema cinema = new Cinema();
                    cinema.setCinemaID(rs.getInt("cinema_id"));
                    cinema.setCinemaName(rs.getString("cinema_name"));
                    cinema.setInfo(rs.getString("info"));
                    return cinema;
                }
            }
        } catch (SQLException e) {
            System.err.println("查詢影院失敗: " + e.getMessage());
        }
        return null;
    }

    public Cinema findCinemaByName(String cinemaName) {
        String sql = "SELECT * FROM cinema WHERE cinema_name = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cinemaName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Cinema cinema = new Cinema();
                    cinema.setCinemaID(rs.getInt("cinema_id"));
                    cinema.setCinemaName(rs.getString("cinema_name"));
                    cinema.setInfo(rs.getString("info"));
                    return cinema;
                }
            }
        } catch (SQLException e) {
            System.err.println("查詢影院失敗: " + e.getMessage());
        }
        return null;
    }
}
