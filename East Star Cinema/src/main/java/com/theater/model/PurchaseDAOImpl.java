package com.theater.model;

import com.theater.utils.DatabaseHelper;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PurchaseDAOImpl implements PurchaseDAO {

    @Override
    public List<Integer> listPurchase(int userID) {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT purchase_id FROM purchases WHERE user_id = ? ORDER BY purchase_id DESC";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) ids.add(rs.getInt("purchase_id"));
            }
        } catch (SQLException e) {
            System.err.println("查詢商品訂單失敗: " + e.getMessage());
        }
        return ids;
    }

    @Override
    public void cancelPurchase(int purchaseID) {
        Purchase purchase = findPurchaseById(purchaseID);
        if (purchase != null) {
            purchase.cancellPurchase(purchaseID);
            ProductDAO productDAO = new ProductDAOImpl();
            productDAO.updateAmount(purchase.getProductID(), 1);
        }
        String sql = "DELETE FROM purchases WHERE purchase_id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, purchaseID);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("取消商品訂單失敗: " + e.getMessage());
        }
    }

    @Override
    public boolean insertPurchase(Purchase purchase) {
        String sql = """
                INSERT INTO purchases (product_id, user_id, order_time, type, sold_price)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, purchase.getProductID());
            ps.setInt(2, purchase.getUserID());
            ps.setString(3, purchase.getOrderTime().toString());
            ps.setString(4, purchase.getType());
            ps.setInt(5, purchase.getSoldPrice());
            if (ps.executeUpdate() > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        purchase.setPurchaseID(keys.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("新增商品訂單失敗: " + e.getMessage());
        }
        return false;
    }

    @Override
    public Purchase findPurchaseById(int purchaseID) {
        String sql = "SELECT * FROM purchases WHERE purchase_id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, purchaseID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return extractPurchase(rs);
            }
        } catch (SQLException e) {
            System.err.println("查詢商品訂單失敗: " + e.getMessage());
        }
        return null;
    }

    private Purchase extractPurchase(ResultSet rs) throws SQLException {
        Purchase purchase = new Purchase();
        purchase.setPurchaseID(rs.getInt("purchase_id"));
        purchase.setProductID(rs.getInt("product_id"));
        purchase.setUserID(rs.getInt("user_id"));
        purchase.setOrderTime(LocalDate.parse(rs.getString("order_time")));
        purchase.setType(rs.getString("type"));
        purchase.setSoldPrice(rs.getInt("sold_price"));
        return purchase;
    }
}
