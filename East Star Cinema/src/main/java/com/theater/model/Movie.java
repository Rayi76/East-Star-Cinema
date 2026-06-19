package com.theater.model;

import java.time.LocalDate;

public class Movie {

    public enum MovieStatus { UNRELEASED, UPCOMING, IN_THEATER, OUT_THEATER }

    private int movieID;
    private String movieName;
    private String info;
    private LocalDate publishDate;
    private LocalDate takeDownDate;
    private MovieStatus status;

    public Movie() {
        this.status = MovieStatus.UNRELEASED;
    }

    public Movie(String movieName, String info, LocalDate publishDate, LocalDate takeDownDate) {
        this.movieName = movieName;
        this.info = info;
        this.publishDate = publishDate;
        this.takeDownDate = takeDownDate;
        this.status = MovieStatus.UNRELEASED;
    }

    public int getMovieID() { return movieID; }
    public void setMovieID(int movieID) { this.movieID = movieID; }

    public String getMovieName() { return movieName; }
    public void setMovieName(String movieName) { this.movieName = movieName; }

    public String getInfo() { return info; }
    public void setInfo(String info) { this.info = info; }

    public LocalDate getPublishDate() { return publishDate; }
    public void setPublishDate(LocalDate publishDate) { this.publishDate = publishDate; }

    public LocalDate getTakeDownDate() { return takeDownDate; }
    public void setTakeDownDate(LocalDate takeDownDate) { this.takeDownDate = takeDownDate; }

    public MovieStatus getStatus() { return status; }
    public void setStatus(MovieStatus status) { this.status = status; }
}
