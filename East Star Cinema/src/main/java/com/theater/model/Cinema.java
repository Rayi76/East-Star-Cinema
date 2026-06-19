package com.theater.model;

public class Cinema {

    private int cinemaID;
    private String cinemaName;
    private String info;

    public Cinema() {}

    public Cinema(String cinemaName, String info) {
        this.cinemaName = cinemaName;
        this.info = info;
    }

    public int getCinemaID() { return cinemaID; }
    public void setCinemaID(int cinemaID) { this.cinemaID = cinemaID; }

    public String getCinemaName() { return cinemaName; }
    public void setCinemaName(String cinemaName) { this.cinemaName = cinemaName; }

    public String getInfo() { return info; }
    public void setInfo(String info) { this.info = info; }
}
