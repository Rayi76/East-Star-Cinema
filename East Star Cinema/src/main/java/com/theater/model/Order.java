package com.theater.model;

import java.time.LocalDateTime;

public class Order {

    public enum TicketType { ADULT, STUDENT, CHILD, CONCESSION }

    public enum OrderStatus { VALID, USED, CANCELLED }

    private int orderID;
    private int userID;
    private int cinemaID;
    private int movieID;
    private int sessionID;
    private String seat;
    private LocalDateTime orderTime;
    private TicketType type;
    private int price;
    private OrderStatus status;

    public Order() {
        this.status = OrderStatus.VALID;
        this.orderTime = LocalDateTime.now();
    }

    /** 新增訂單（訂票）時建構 Order 物件 */
    public Order(int userID, int cinemaID, int movieID, int sessionID, String seat,
                 LocalDateTime orderTime, TicketType type, int price) {
        this.userID = userID;
        this.cinemaID = cinemaID;
        this.movieID = movieID;
        this.sessionID = sessionID;
        this.seat = seat;
        this.orderTime = orderTime != null ? orderTime : LocalDateTime.now();
        this.type = type;
        this.price = price;
        this.status = OrderStatus.VALID;
    }

    /** 取消訂單，將 status 改為 CANCELLED */
    public int cancellOrder(int orderID) {
        if (this.orderID == orderID && status == OrderStatus.VALID) {
            status = OrderStatus.CANCELLED;
            return 1;
        }
        return 0;
    }

    public void checkOrderStatus(int orderID) {
        if (this.orderID == orderID) {
            System.out.println("訂單 " + orderID + " 狀態：" + status);
        }
    }

    public void changeOrderStatus(int orderID, OrderStatus status) {
        if (this.orderID == orderID) {
            this.status = status;
        }
    }

    public int getOrderID() { return orderID; }
    public void setOrderID(int orderID) { this.orderID = orderID; }

    public int getUserID() { return userID; }
    public void setUserID(int userID) { this.userID = userID; }

    public int getCinemaID() { return cinemaID; }
    public void setCinemaID(int cinemaID) { this.cinemaID = cinemaID; }

    public int getMovieID() { return movieID; }
    public void setMovieID(int movieID) { this.movieID = movieID; }

    public int getSessionID() { return sessionID; }
    public void setSessionID(int sessionID) { this.sessionID = sessionID; }

    public String getSeat() { return seat; }
    public void setSeat(String seat) { this.seat = seat; }

    public LocalDateTime getOrderTime() { return orderTime; }
    public void setOrderTime(LocalDateTime orderTime) { this.orderTime = orderTime; }

    public TicketType getType() { return type; }
    public void setType(TicketType type) { this.type = type; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
}
