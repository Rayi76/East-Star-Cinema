package com.theater.model;

import java.time.LocalDateTime;

public class Session {

    public enum SessionStatus { Not_on_sale, Selling, Sold_out }

    private int sessionID;
    private int movieID;
    private int cinemaID;
    private int auditoriumID;
    private LocalDateTime sessionTime;
    private SessionStatus status;
    private int seatSold;

    public Session() {
        this.status = SessionStatus.Not_on_sale;
        this.seatSold = 0;
    }

    /** 管理員新增場次時建構 Session 物件 */
    public Session(int movieID, int cinemaID, int auditoriumID, LocalDateTime sessionTime) {
        this.movieID = movieID;
        this.cinemaID = cinemaID;
        this.auditoriumID = auditoriumID;
        this.sessionTime = sessionTime;
        this.status = SessionStatus.Not_on_sale;
        this.seatSold = 0;
    }

    /** 更改已售座位數量 */
    public void updateSoldSeat(int count) {
        this.seatSold += count;
        if (seatSold < 0) {
            seatSold = 0;
        }
    }

    public int getSessionID() { return sessionID; }
    public void setSessionID(int sessionID) { this.sessionID = sessionID; }

    public int getMovieID() { return movieID; }
    public void setMovieID(int movieID) { this.movieID = movieID; }

    public int getCinemaID() { return cinemaID; }
    public void setCinemaID(int cinemaID) { this.cinemaID = cinemaID; }

    public int getAuditoriumID() { return auditoriumID; }
    public void setAuditoriumID(int auditoriumID) { this.auditoriumID = auditoriumID; }

    public LocalDateTime getSessionTime() { return sessionTime; }
    public void setSessionTime(LocalDateTime sessionTime) { this.sessionTime = sessionTime; }

    public SessionStatus getStatus() { return status; }
    public void setStatus(SessionStatus status) { this.status = status; }

    public int getSeatSold() { return seatSold; }
    public void setSeatSold(int seatSold) { this.seatSold = seatSold; }
}
