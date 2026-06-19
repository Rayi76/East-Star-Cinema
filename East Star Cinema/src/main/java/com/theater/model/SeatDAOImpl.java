package com.theater.model;

import com.theater.utils.DatabaseHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SeatDAOImpl implements SeatDAO {

    @Override
    public List<Integer> listSellingSeat(int sessionID) {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT seat_id FROM seats WHERE session_id = ? AND status = 'SELLING'";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sessionID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) ids.add(rs.getInt("seat_id"));
            }
        } catch (SQLException e) {
            System.err.println("查詢可售座位失敗: " + e.getMessage());
        }
        return ids;
    }

    @Override
    public void lockSoldSeat(int seatID, int orderID) {
        String sql = "UPDATE seats SET status = ?, order_id = ? WHERE seat_id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, Seat.SeatStatus.SOLD_OUT.name());
            ps.setInt(2, orderID);
            ps.setInt(3, seatID);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("鎖定座位失敗: " + e.getMessage());
        }
    }

    @Override
    public void releaseSeat(int seatID) {
        String sql = "UPDATE seats SET status = ?, order_id = NULL WHERE seat_id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, Seat.SeatStatus.SELLING.name());
            ps.setInt(2, seatID);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("釋放座位失敗: " + e.getMessage());
        }
    }

    @Override
    public void initSessionSeats(int sessionID, int rows, int cols) {
        String sql = "INSERT INTO seats (session_id, seat_code, priority, status, order_id) VALUES (?, ?, ?, ?, NULL)";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);
            for (int r = 1; r <= rows; r++) {
                char rowLetter = (char) ('A' + (r - 1));
                for (int c = 1; c <= cols; c++) {
                    String seatCode = String.format("%c%02d", rowLetter, c);
                    ps.setInt(1, sessionID);
                    ps.setString(2, seatCode);
                    ps.setInt(3, 1);
                    ps.setString(4, Seat.SeatStatus.SELLING.name());
                    ps.addBatch();
                }
            }
            ps.executeBatch();
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            System.err.println("初始化座位失敗: " + e.getMessage());
        }
    }

    @Override
    public Seat findSeatById(int seatID) {
        String sql = "SELECT * FROM seats WHERE seat_id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, seatID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return extractSeat(rs);
            }
        } catch (SQLException e) {
            System.err.println("查詢座位失敗: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Seat findSeatByCode(int sessionID, String seatCode) {
        String sql = "SELECT * FROM seats WHERE session_id = ? AND seat_code = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sessionID);
            ps.setString(2, seatCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return extractSeat(rs);
            }
        } catch (SQLException e) {
            System.err.println("查詢座位失敗: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Seat> listSeatsBySession(int sessionID) {
        List<Seat> seats = new ArrayList<>();
        String sql = "SELECT * FROM seats WHERE session_id = ? ORDER BY seat_code";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sessionID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) seats.add(extractSeat(rs));
            }
        } catch (SQLException e) {
            System.err.println("查詢座位列表失敗: " + e.getMessage());
        }
        return seats;
    }

    private Seat extractSeat(ResultSet rs) throws SQLException {
        Seat seat = new Seat();
        seat.setSeatID(rs.getInt("seat_id"));
        seat.setSessionID(rs.getInt("session_id"));
        seat.setSeatCode(rs.getString("seat_code"));
        seat.setPriority(rs.getInt("priority"));
        seat.setStatus(Seat.SeatStatus.valueOf(rs.getString("status")));
        int orderId = rs.getInt("order_id");
        if (!rs.wasNull()) seat.setOrderID(orderId);
        return seat;
    }
}
