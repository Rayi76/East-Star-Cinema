package com.theater.model;

import com.theater.utils.DatabaseHelper;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProductDAOImpl implements ProductDAO {

    @Override
    public List<Integer> listProduct(int movieID) {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT product_id FROM products WHERE movie_id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, movieID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) ids.add(rs.getInt("product_id"));
            }
        } catch (SQLException e) {
            System.err.println("查詢商品失敗: " + e.getMessage());
        }
        return ids;
    }

    @Override
    public void updateAmount(int productID, int change) {
        Product product = findProductById(productID);
        if (product == null) return;
        product.updateAmount(productID, change);
        String sql = "UPDATE products SET amount = ?, status = ? WHERE product_id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, product.getAmount());
            ps.setString(2, product.getStatus().name());
            ps.setInt(3, productID);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("更新商品數量失敗: " + e.getMessage());
        }
    }

    @Override
    public void setStatus(int productID, Product.ProductStatus newStatus) {
        String sql = "UPDATE products SET status = ? WHERE product_id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus.name());
            ps.setInt(2, productID);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("更新商品狀態失敗: " + e.getMessage());
        }
    }

    @Override
    public boolean insertProduct(Product product) {
        String sql = """
                INSERT INTO products (product_name, movie_id, info, price, publish_time, amount, status)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, product.getProductName());
            ps.setInt(2, product.getMovieID());
            ps.setString(3, product.getInfo());
            ps.setInt(4, product.getPrice());
            ps.setString(5, product.getPublishTime().toString());
            ps.setInt(6, product.getAmount());
            ps.setString(7, product.getStatus().name());
            if (ps.executeUpdate() > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        product.setProductID(keys.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("新增商品失敗: " + e.getMessage());
        }
        return false;
    }

    @Override
    public Product findProductById(int productID) {
        String sql = "SELECT * FROM products WHERE product_id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return extractProduct(rs);
            }
        } catch (SQLException e) {
            System.err.println("查詢商品失敗: " + e.getMessage());
        }
        return null;
    }

    private Product extractProduct(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setProductID(rs.getInt("product_id"));
        product.setProductName(rs.getString("product_name"));
        product.setMovieID(rs.getInt("movie_id"));
        product.setInfo(rs.getString("info"));
        product.setPrice(rs.getInt("price"));
        product.setPublishTime(LocalDate.parse(rs.getString("publish_time")));
        product.setAmount(rs.getInt("amount"));
        product.setStatus(Product.ProductStatus.valueOf(rs.getString("status")));
        return product;
    }
}
