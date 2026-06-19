package com.theater.model;

import java.util.List;

public interface CinemaDAO {
    List<String> listCinema();
    boolean insertCinema(Cinema cinema);
    Cinema findCinemaById(int cinemaID);
}
