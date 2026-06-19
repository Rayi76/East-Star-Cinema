package com.theater.model;

import com.theater.utils.DatabaseHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuditoriumDAOImpl implements AuditoriumDAO {

    @Override
    public boolean insertAuditorium(Auditorium auditorium, int cinemaID) {
        String seatCodes = String.join(",", auditorium.getSeat());
        String sql = """
                INSERT INTO auditoriums (cinema_id, auditorium_name, seat_amount, auditorium_type, seat_codes)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, cinemaID);
            ps.setString(2, auditorium.getAuditoriumName());
            ps.setInt(3, auditorium.getSeatAmount());
            ps.setString(4, auditorium.getAuditoriumType().name());
            ps.setString(5, seatCodes);
            if (ps.executeUpdate() > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        auditorium.setAuditoriumID(keys.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("新增影廳失敗: " + e.getMessage());
        }
        return false;
    }

    @Override
    public List<Auditorium> listAuditoriumsByCinema(int cinemaID) {
        List<Auditorium> list = new ArrayList<>();
        String sql = "SELECT * FROM auditoriums WHERE cinema_id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cinemaID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(extractAuditorium(rs));
            }
        } catch (SQLException e) {
            System.err.println("查詢影廳失敗: " + e.getMessage());
        }
        return list;
    }

    @Override
    public Auditorium findAuditoriumById(int auditoriumID) {
        String sql = "SELECT * FROM auditoriums WHERE auditorium_id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, auditoriumID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return extractAuditorium(rs);
            }
        } catch (SQLException e) {
            System.err.println("查詢影廳失敗: " + e.getMessage());
        }
        return null;
    }

    private Auditorium extractAuditorium(ResultSet rs) throws SQLException {
        Auditorium auditorium = new Auditorium();
        auditorium.setAuditoriumID(rs.getInt("auditorium_id"));
        auditorium.setAuditoriumName(rs.getString("auditorium_name"));
        auditorium.setSeatAmount(rs.getInt("seat_amount"));
        auditorium.setAuditoriumType(Auditorium.AuditoriumType.valueOf(rs.getString("auditorium_type")));
        String codes = rs.getString("seat_codes");
        auditorium.setSeat(codes != null ? codes.split(",") : new String[0]);
        return auditorium;
    }
}
