package com.theater.model;

public class Seat {

    public enum SeatStatus { SELLING, SOLD_OUT }

    private int seatID;
    private int sessionID;
    private String seatCode;
    private int priority;
    private SeatStatus status;
    private Integer orderID;

    public Seat() {
        this.status = SeatStatus.SELLING;
        this.priority = 1;
    }

    public Seat(String seatCode, int priority, SeatStatus status, Integer orderID) {
        this.seatCode = seatCode;
        this.priority = priority;
        this.status = status != null ? status : SeatStatus.SELLING;
        this.orderID = orderID;
    }

    /** 回傳座位是否可買 */
    public boolean isAvailable() {
        return status == SeatStatus.SELLING && orderID == null;
    }

    /** 修改座位狀態為已販售 */
    public void lockSoldSeat(int orderID) {
        this.status = SeatStatus.SOLD_OUT;
        this.orderID = orderID;
    }

    /** 修改座位狀態為販售中 */
    public void releaseSeat() {
        this.status = SeatStatus.SELLING;
        this.orderID = null;
    }

    public int getSeatID() { return seatID; }
    public void setSeatID(int seatID) { this.seatID = seatID; }

    public int getSessionID() { return sessionID; }
    public void setSessionID(int sessionID) { this.sessionID = sessionID; }

    public String getSeatCode() { return seatCode; }
    public void setSeatCode(String seatCode) { this.seatCode = seatCode; }

    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }

    public SeatStatus getStatus() { return status; }
    public void setStatus(SeatStatus status) { this.status = status; }

    public Integer getOrderID() { return orderID; }
    public void setOrderID(Integer orderID) { this.orderID = orderID; }
}
