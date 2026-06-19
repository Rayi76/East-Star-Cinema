package com.theater.model;

import java.util.List;

public interface AuditoriumDAO {
    boolean insertAuditorium(Auditorium auditorium, int cinemaID);
    List<Auditorium> listAuditoriumsByCinema(int cinemaID);
    Auditorium findAuditoriumById(int auditoriumID);
}
