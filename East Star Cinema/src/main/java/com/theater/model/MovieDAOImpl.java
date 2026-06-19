package com.theater.model;

import com.theater.utils.DatabaseHelper;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MovieDAOImpl implements MovieDAO {

    @Override
    public List<String> listMovie(Movie.MovieStatus status) {
        List<String> names = new ArrayList<>();
        String sql = status == null
                ? "SELECT movie_name FROM movies ORDER BY movie_id"
                : "SELECT movie_name FROM movies WHERE status = ? ORDER BY movie_id";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (status != null) ps.setString(1, status.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) names.add(rs.getString("movie_name"));
            }
        } catch (SQLException e) {
            System.err.println("查詢電影列表失敗: " + e.getMessage());
        }
        return names;
    }

    @Override
    public boolean insertMovie(Movie movie) {
        String sql = """
                INSERT INTO movies (movie_name, info, publish_date, take_down_date, status)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, movie.getMovieName());
            ps.setString(2, movie.getInfo());
            ps.setString(3, movie.getPublishDate().toString());
            ps.setString(4, movie.getTakeDownDate().toString());
            ps.setString(5, movie.getStatus().name());
            if (ps.executeUpdate() > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        movie.setMovieID(keys.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("新增電影失敗: " + e.getMessage());
        }
        return false;
    }

    @Override
    public Movie findMovieById(int movieID) {
        String sql = "SELECT * FROM movies WHERE movie_id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, movieID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return extractMovie(rs);
            }
        } catch (SQLException e) {
            System.err.println("查詢電影失敗: " + e.getMessage());
        }
        return null;
    }

    public Movie findMovieByName(String movieName) {
        String sql = "SELECT * FROM movies WHERE movie_name = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, movieName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return extractMovie(rs);
            }
        } catch (SQLException e) {
            System.err.println("查詢電影失敗: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void updateMovieStatus(int movieID, Movie.MovieStatus status) {
        String sql = "UPDATE movies SET status = ? WHERE movie_id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setInt(2, movieID);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("更新電影狀態失敗: " + e.getMessage());
        }
    }

    private Movie extractMovie(ResultSet rs) throws SQLException {
        Movie movie = new Movie();
        movie.setMovieID(rs.getInt("movie_id"));
        movie.setMovieName(rs.getString("movie_name"));
        movie.setInfo(rs.getString("info"));
        movie.setPublishDate(LocalDate.parse(rs.getString("publish_date")));
        movie.setTakeDownDate(LocalDate.parse(rs.getString("take_down_date")));
        movie.setStatus(Movie.MovieStatus.valueOf(rs.getString("status")));
        return movie;
    }
}
