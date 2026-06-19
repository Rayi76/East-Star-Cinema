package com.theater.controller;

import com.theater.model.*;
import com.theater.utils.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class AdminDashboardController {

    @FXML private TextField movieNameInput;
    @FXML private TextArea movieInfoInput;
    @FXML private ComboBox<Movie.MovieStatus> movieStatusCombo;
    @FXML private ComboBox<String> cinemaCombo;
    @FXML private ComboBox<String> auditoriumCombo;
    @FXML private DatePicker sessionDatePicker;
    @FXML private TextField sessionHourInput;
    @FXML private ComboBox<String> sessionMovieCombo;
    @FXML private TextField productNameInput;
    @FXML private TextField productPriceInput;
    @FXML private TextField productAmountInput;
    @FXML private ComboBox<String> productMovieCombo;
    @FXML private Label statusLabel;
    @FXML private ListView<String> movieListView;

    private final MovieDAO movieDAO = new MovieDAOImpl();
    private final MovieDAOImpl movieDAOImpl = new MovieDAOImpl();
    private final CinemaDAO cinemaDAO = new CinemaDAOImpl();
    private final CinemaDAOImpl cinemaDAOImpl = new CinemaDAOImpl();
    private final AuditoriumDAO auditoriumDAO = new AuditoriumDAOImpl();
    private final SessionDAO sessionDAO = new SessionDAOImpl();
    private final SeatDAO seatDAO = new SeatDAOImpl();
    private final ProductDAO productDAO = new ProductDAOImpl();

    @FXML
    public void initialize() {
        movieStatusCombo.getItems().setAll(Movie.MovieStatus.values());
        movieStatusCombo.setValue(Movie.MovieStatus.IN_THEATER);
        cinemaCombo.getItems().setAll(cinemaDAO.listCinema());
        sessionMovieCombo.getItems().setAll(movieDAO.listMovie(null));
        productMovieCombo.getItems().setAll(movieDAO.listMovie(null));
        sessionDatePicker.setValue(LocalDate.now());
        sessionHourInput.setText("14");
        refreshMovieList();
        cinemaCombo.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> loadAuditoriums());
    }

    private void loadAuditoriums() {
        auditoriumCombo.getItems().clear();
        String cinemaName = cinemaCombo.getSelectionModel().getSelectedItem();
        if (cinemaName == null) return;
        Cinema cinema = cinemaDAOImpl.findCinemaByName(cinemaName);
        if (cinema == null) return;
        for (Auditorium a : auditoriumDAO.listAuditoriumsByCinema(cinema.getCinemaID())) {
            auditoriumCombo.getItems().add(a.getAuditoriumName());
        }
    }

    private void refreshMovieList() {
        movieListView.getItems().setAll(movieDAO.listMovie(null));
    }

    @FXML
    public void addMovieAction() {
        String name = movieNameInput.getText().trim();
        if (name.isEmpty()) {
            statusLabel.setText("請輸入電影名稱");
            return;
        }
        Movie movie = new Movie(name, movieInfoInput.getText(),
                LocalDate.now(), LocalDate.now().plusMonths(2));
        movie.setStatus(movieStatusCombo.getValue());
        if (movieDAO.insertMovie(movie)) {
            statusLabel.setText("電影新增成功");
            sessionMovieCombo.getItems().setAll(movieDAO.listMovie(null));
            productMovieCombo.getItems().setAll(movieDAO.listMovie(null));
            refreshMovieList();
            movieNameInput.clear();
            movieInfoInput.clear();
        }
    }

    @FXML
    public void addSessionAction() {
        String movieName = sessionMovieCombo.getSelectionModel().getSelectedItem();
        String cinemaName = cinemaCombo.getSelectionModel().getSelectedItem();
        String auditoriumName = auditoriumCombo.getSelectionModel().getSelectedItem();
        if (movieName == null || cinemaName == null || auditoriumName == null) {
            statusLabel.setText("請選擇電影、影院與影廳");
            return;
        }
        Movie movie = movieDAOImpl.findMovieByName(movieName);
        Cinema cinema = cinemaDAOImpl.findCinemaByName(cinemaName);
        Auditorium auditorium = findAuditorium(cinema.getCinemaID(), auditoriumName);
        if (movie == null || cinema == null || auditorium == null) return;

        int hour;
        try {
            hour = Integer.parseInt(sessionHourInput.getText().trim());
        } catch (NumberFormatException e) {
            statusLabel.setText("場次時間格式錯誤");
            return;
        }

        LocalDateTime sessionTime = LocalDateTime.of(
                sessionDatePicker.getValue(), LocalTime.of(hour, 0));
        Session session = new Session(movie.getMovieID(), cinema.getCinemaID(),
                auditorium.getAuditoriumID(), sessionTime);
        session.setStatus(Session.SessionStatus.Selling);

        if (sessionDAO.insertSession(session)) {
            seatDAO.initSessionSeats(session.getSessionID(), 8, 10);
            statusLabel.setText("場次新增成功，座位已初始化");
        }
    }

    @FXML
    public void addProductAction() {
        String movieName = productMovieCombo.getSelectionModel().getSelectedItem();
        String productName = productNameInput.getText().trim();
        if (movieName == null || productName.isEmpty()) {
            statusLabel.setText("請選擇電影並輸入商品名稱");
            return;
        }
        int price, amount;
        try {
            price = Integer.parseInt(productPriceInput.getText().trim());
            amount = Integer.parseInt(productAmountInput.getText().trim());
        } catch (NumberFormatException e) {
            statusLabel.setText("價格或數量格式錯誤");
            return;
        }
        Movie movie = movieDAOImpl.findMovieByName(movieName);
        if (movie == null) return;

        Product product = new Product(productName, movie.getMovieID(), "",
                price, LocalDate.now(), amount, Product.ProductStatus.SELLING);
        if (productDAO.insertProduct(product)) {
            statusLabel.setText("商品新增成功");
            productNameInput.clear();
            productPriceInput.clear();
            productAmountInput.clear();
        }
    }

    @FXML
    public void logoutAction(ActionEvent event) {
        UserSession.getInstance().logout();
        AppController.switchScene(event, "/com/theater/login.fxml", "Theater - 登入");
    }

    private Auditorium findAuditorium(int cinemaId, String name) {
        for (Auditorium a : auditoriumDAO.listAuditoriumsByCinema(cinemaId)) {
            if (a.getAuditoriumName().equals(name)) return a;
        }
        return null;
    }
}
