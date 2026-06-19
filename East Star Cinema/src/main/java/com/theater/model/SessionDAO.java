package com.theater.model;

import java.util.List;

public interface SessionDAO {
    List<Integer> findSession(int movieID, boolean sign);
    List<Integer> findSession(int cinemaID, int sign);
    void updateSoldSeat(int sessionID, int count);
    boolean insertSession(Session session);
    Session findSessionById(int sessionID);
    void updateSessionStatus(int sessionID, Session.SessionStatus status);
}
