package com.theater.model;

import com.theater.utils.DatabaseHelper;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderDAOImpl implements OrderDAO {

    @Override
    public List<Integer> findOrder(int userID) {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT order_id FROM orders WHERE user_id = ? ORDER BY order_id DESC";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) ids.add(rs.getInt("order_id"));
            }
        } catch (SQLException e) {
            System.err.println("查詢訂單失敗: " + e.getMessage());
        }
        return ids;
    }

    @Override
    public int cancelOrder(int orderID) {
        String sql = "UPDATE orders SET status = ? WHERE order_id = ? AND status = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, Order.OrderStatus.CANCELLED.name());
            ps.setInt(2, orderID);
            ps.setString(3, Order.OrderStatus.VALID.name());
            return ps.executeUpdate() > 0 ? 1 : 0;
        } catch (SQLException e) {
            System.err.println("取消訂單失敗: " + e.getMessage());
        }
        return 0;
    }

    @Override
    public void checkOrderStatus(int orderID) {
        Order order = findOrderById(orderID);
        if (order != null) {
            order.checkOrderStatus(orderID);
        }
    }

    @Override
    public void changeOrderStatus(int orderID, Order.OrderStatus status) {
        String sql = "UPDATE orders SET status = ? WHERE order_id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setInt(2, orderID);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("變更訂單狀態失敗: " + e.getMessage());
        }
    }

    @Override
    public boolean createOrder(Order order) {
        String sql = """
                INSERT INTO orders (user_id, cinema_id, movie_id, session_id, seat, order_time, type, price, status)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, order.getUserID());
            ps.setInt(2, order.getCinemaID());
            ps.setInt(3, order.getMovieID());
            ps.setInt(4, order.getSessionID());
            ps.setString(5, order.getSeat());
            ps.setString(6, order.getOrderTime().toString());
            ps.setString(7, order.getType().name());
            ps.setInt(8, order.getPrice());
            ps.setString(9, order.getStatus().name());
            if (ps.executeUpdate() > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        order.setOrderID(keys.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("建立訂單失敗: " + e.getMessage());
        }
        return false;
    }

    @Override
    public Order findOrderById(int orderID) {
        String sql = "SELECT * FROM orders WHERE order_id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return extractOrder(rs);
            }
        } catch (SQLException e) {
            System.err.println("查詢訂單失敗: " + e.getMessage());
        }
        return null;
    }

    private Order extractOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setOrderID(rs.getInt("order_id"));
        order.setUserID(rs.getInt("user_id"));
        order.setCinemaID(rs.getInt("cinema_id"));
        order.setMovieID(rs.getInt("movie_id"));
        order.setSessionID(rs.getInt("session_id"));
        order.setSeat(rs.getString("seat"));
        order.setOrderTime(LocalDateTime.parse(rs.getString("order_time")));
        order.setType(Order.TicketType.valueOf(rs.getString("type")));
        order.setPrice(rs.getInt("price"));
        order.setStatus(Order.OrderStatus.valueOf(rs.getString("status")));
        return order;
    }
}
