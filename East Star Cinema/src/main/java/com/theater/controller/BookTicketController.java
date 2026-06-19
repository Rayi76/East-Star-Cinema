package com.theater.controller;

import com.theater.model.*;
import com.theater.utils.DateFormatUtil;
import com.theater.utils.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.time.LocalDateTime;
import java.util.List;

public class BookTicketController {

    private static int selectedMovieId = -1;

    @FXML private Label movieLabel;
    @FXML private ComboBox<String> cinemaCombo;
    @FXML private ComboBox<String> sessionCombo;
    @FXML private ComboBox<Order.TicketType> ticketTypeCombo;
    @FXML private GridPane seatGrid;
    @FXML private Label statusLabel;
    @FXML private Label priceLabel;

    private final CinemaDAO cinemaDAO = new CinemaDAOImpl();
    private final CinemaDAOImpl cinemaDAOImpl = new CinemaDAOImpl();
    private final SessionDAO sessionDAO = new SessionDAOImpl();
    private final SeatDAO seatDAO = new SeatDAOImpl();
    private final OrderDAO orderDAO = new OrderDAOImpl();
    private final UserDAO userDAO = new UserDAOImpl();
    private final MovieDAOImpl movieDAO = new MovieDAOImpl();

    private int selectedSessionId = -1;
    private String selectedSeatCode = null;
    private ToggleGroup seatGroup = new ToggleGroup();

    public static void setSelectedMovieId(int movieId) {
        selectedMovieId = movieId;
    }

    @FXML
    public void initialize() {
        ticketTypeCombo.getItems().setAll(Order.TicketType.values());
        ticketTypeCombo.setValue(Order.TicketType.ADULT);
        ticketTypeCombo.setOnAction(e -> updatePrice());

        Movie movie = movieDAO.findMovieById(selectedMovieId);
        movieLabel.setText(movie != null ? "電影：" + movie.getMovieName() : "電影");

        cinemaCombo.getItems().setAll(cinemaDAO.listCinema());
        cinemaCombo.setOnAction(e -> loadSessions());
    }

    private void loadSessions() {
        sessionCombo.getItems().clear();
        seatGrid.getChildren().clear();
        selectedSessionId = -1;
        selectedSeatCode = null;

        String cinemaName = cinemaCombo.getSelectionModel().getSelectedItem();
        if (cinemaName == null) return;
        Cinema cinema = cinemaDAOImpl.findCinemaByName(cinemaName);
        if (cinema == null) return;

        for (int sid : sessionDAO.findSession(selectedMovieId, true)) {
            Session session = sessionDAO.findSessionById(sid);
            if (session != null && session.getCinemaID() == cinema.getCinemaID()) {
                sessionCombo.getItems().add(String.format("#%d  %s",
                        sid, session.getSessionTime().format(DateFormatUtil.DATETIME)));
            }
        }
        sessionCombo.setOnAction(e -> loadSeats());
    }

    private void loadSeats() {
        seatGrid.getChildren().clear();
        selectedSeatCode = null;
        seatGroup = new ToggleGroup();

        String selected = sessionCombo.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        selectedSessionId = Integer.parseInt(selected.substring(1, selected.indexOf(' ')));

        List<Seat> seats = seatDAO.listSeatsBySession(selectedSessionId);
        int col = 0, row = 0;
        for (Seat seat : seats) {
            ToggleButton btn = new ToggleButton(seat.getSeatCode());
            btn.setToggleGroup(seatGroup);
            btn.setPrefWidth(50);
            btn.setDisable(!seat.isAvailable());
            if (!seat.isAvailable()) {
                btn.setStyle("-fx-background-color: #cccccc;");
            }
            btn.setOnAction(e -> {
                selectedSeatCode = seat.getSeatCode();
                updatePrice();
            });
            seatGrid.add(btn, col, row);
            col++;
            if (col >= 10) {
                col = 0;
                row++;
            }
        }
        updatePrice();
    }

    private void updatePrice() {
        int price = getTicketPrice(ticketTypeCombo.getValue());
        priceLabel.setText("票價：$" + price);
    }

    private int getTicketPrice(Order.TicketType type) {
        return switch (type) {
            case ADULT -> 350;
            case STUDENT -> 280;
            case CHILD -> 200;
            case CONCESSION -> 250;
        };
    }

    @FXML
    public void confirmBookingAction() {
        if (selectedSessionId == -1 || selectedSeatCode == null) {
            statusLabel.setText("請選擇場次與座位");
            return;
        }
        User user = UserSession.getInstance().getCurrentUser();
        Session session = sessionDAO.findSessionById(selectedSessionId);
        Seat seat = seatDAO.findSeatByCode(selectedSessionId, selectedSeatCode);
        if (user == null || session == null || seat == null || !seat.isAvailable()) {
            statusLabel.setText("座位不可用");
            return;
        }

        Order.TicketType type = ticketTypeCombo.getValue();
        int price = getTicketPrice(type);
        Order order = new Order(user.getUserID(), session.getCinemaID(), session.getMovieID(),
                selectedSessionId, selectedSeatCode, LocalDateTime.now(), type, price);

        if (orderDAO.createOrder(order)) {
            seatDAO.lockSoldSeat(seat.getSeatID(), order.getOrderID());
            sessionDAO.updateSoldSeat(selectedSessionId, 1);
            userDAO.updateConsumption(user.getUserID(), price);
            userDAO.updateStardust(user.getUserID(), price / 10);
            UserSession.getInstance().loginUser(userDAO.findUserById(user.getUserID()));
            statusLabel.setText("訂票成功！訂單編號 #" + order.getOrderID());
            loadSeats();
        } else {
            statusLabel.setText("訂票失敗");
        }
    }

    @FXML
    public void backAction(ActionEvent event) {
        AppController.switchScene(event, "/com/theater/user_dashboard.fxml",
                "Theater - 會員中心");
    }
}
