package com.theater.model;

public class Auditorium {

    public enum AuditoriumType { NORMAL_S, NORMAL_L, THREE_D_S, THREE_D_L, FOUR_DX, IMAX }

    private int auditoriumID;
    private String auditoriumName;
    private int seatAmount;
    private AuditoriumType auditoriumType;
    private String[] seat;

    public Auditorium() {}

    public Auditorium(String auditoriumName, int seatAmount, AuditoriumType auditoriumType, String[] seat) {
        this.auditoriumName = auditoriumName;
        this.seatAmount = seatAmount;
        this.auditoriumType = auditoriumType;
        this.seat = seat;
    }

    /** 依影廳類型回傳座位總數 */
    public static int getTotalSeatsCount(AuditoriumType type) {
        return switch (type) {
            case NORMAL_S, THREE_D_S -> 80;
            case NORMAL_L, THREE_D_L, FOUR_DX, IMAX -> 200;
        };
    }

    public int getAuditoriumID() { return auditoriumID; }
    public void setAuditoriumID(int auditoriumID) { this.auditoriumID = auditoriumID; }

    public String getAuditoriumName() { return auditoriumName; }
    public void setAuditoriumName(String auditoriumName) { this.auditoriumName = auditoriumName; }

    public int getSeatAmount() { return seatAmount; }
    public void setSeatAmount(int seatAmount) { this.seatAmount = seatAmount; }

    public AuditoriumType getAuditoriumType() { return auditoriumType; }
    public void setAuditoriumType(AuditoriumType auditoriumType) { this.auditoriumType = auditoriumType; }

    public String[] getSeat() { return seat; }
    public void setSeat(String[] seat) { this.seat = seat; }
}
