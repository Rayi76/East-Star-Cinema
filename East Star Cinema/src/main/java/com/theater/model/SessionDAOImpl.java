package com.theater.model;

import com.theater.utils.DatabaseHelper;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SessionDAOImpl implements SessionDAO {

    @Override
    public List<Integer> findSession(int movieID, boolean sign) {
        List<Integer> ids = new ArrayList<>();
        String sql = sign
                ? "SELECT session_id FROM sessions WHERE movie_id = ? AND status = 'Selling'"
                : "SELECT session_id FROM sessions WHERE movie_id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, movieID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) ids.add(rs.getInt("session_id"));
            }
        } catch (SQLException e) {
            System.err.println("查詢場次失敗: " + e.getMessage());
        }
        return ids;
    }

    @Override
    public List<Integer> findSession(int cinemaID, int sign) {
        List<Integer> ids = new ArrayList<>();
        String sql = sign > 0
                ? "SELECT session_id FROM sessions WHERE cinema_id = ? AND status = 'Selling'"
                : "SELECT session_id FROM sessions WHERE cinema_id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cinemaID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) ids.add(rs.getInt("session_id"));
            }
        } catch (SQLException e) {
            System.err.println("查詢場次失敗: " + e.getMessage());
        }
        return ids;
    }

    @Override
    public void updateSoldSeat(int sessionID, int count) {
        String sql = "UPDATE sessions SET seat_sold = seat_sold + ? WHERE session_id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, count);
            ps.setInt(2, sessionID);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("更新已售座位失敗: " + e.getMessage());
        }
    }

    @Override
    public boolean insertSession(Session session) {
        String sql = """
                INSERT INTO sessions (movie_id, cinema_id, auditorium_id, session_time, status, seat_sold)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, session.getMovieID());
            ps.setInt(2, session.getCinemaID());
            ps.setInt(3, session.getAuditoriumID());
            ps.setString(4, session.getSessionTime().toString());
            ps.setString(5, session.getStatus().name());
            ps.setInt(6, session.getSeatSold());
            if (ps.executeUpdate() > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        session.setSessionID(keys.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("新增場次失敗: " + e.getMessage());
        }
        return false;
    }

    @Override
    public Session findSessionById(int sessionID) {
        String sql = "SELECT * FROM sessions WHERE session_id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sessionID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return extractSession(rs);
            }
        } catch (SQLException e) {
            System.err.println("查詢場次失敗: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void updateSessionStatus(int sessionID, Session.SessionStatus status) {
        String sql = "UPDATE sessions SET status = ? WHERE session_id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setInt(2, sessionID);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("更新場次狀態失敗: " + e.getMessage());
        }
    }

    private Session extractSession(ResultSet rs) throws SQLException {
        Session session = new Session();
        session.setSessionID(rs.getInt("session_id"));
        session.setMovieID(rs.getInt("movie_id"));
        session.setCinemaID(rs.getInt("cinema_id"));
        session.setAuditoriumID(rs.getInt("auditorium_id"));
        session.setSessionTime(LocalDateTime.parse(rs.getString("session_time")));
        session.setStatus(Session.SessionStatus.valueOf(rs.getString("status")));
        session.setSeatSold(rs.getInt("seat_sold"));
        return session;
    }
}
