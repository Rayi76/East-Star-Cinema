package com.theater.model;

import java.util.List;

public interface SeatDAO {
    List<Integer> listSellingSeat(int sessionID);
    void lockSoldSeat(int seatID, int orderID);
    void releaseSeat(int seatID);
    void initSessionSeats(int sessionID, int rows, int cols);
    Seat findSeatById(int seatID);
    Seat findSeatByCode(int sessionID, String seatCode);
    List<Seat> listSeatsBySession(int sessionID);
}
