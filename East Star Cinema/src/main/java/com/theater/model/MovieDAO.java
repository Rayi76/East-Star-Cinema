package com.theater.model;

import java.util.List;

public interface MovieDAO {
    List<String> listMovie(Movie.MovieStatus status);
    boolean insertMovie(Movie movie);
    Movie findMovieById(int movieID);
    void updateMovieStatus(int movieID, Movie.MovieStatus status);
}
