package com.theater.controller;

import com.theater.model.*;
import com.theater.utils.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class UserDashboardController {

    @FXML private Label welcomeLabel;
    @FXML private Label memberInfoLabel;
    @FXML private ListView<String> movieListView;
    @FXML private ListView<String> orderListView;
    @FXML private ListView<String> productListView;
    @FXML private Label statusLabel;

    private final MovieDAO movieDAO = new MovieDAOImpl();
    private final OrderDAO orderDAO = new OrderDAOImpl();
    private final ProductDAO productDAO = new ProductDAOImpl();
    private final MovieDAOImpl movieDAOImpl = new MovieDAOImpl();

    @FXML
    public void initialize() {
        User user = UserSession.getInstance().getCurrentUser();
        if (user == null) return;

        welcomeLabel.setText("歡迎，" + user.getUsername());
        memberInfoLabel.setText(String.format("等級：%s  |  星塵：%d  |  本月消費：$%d",
                user.getLevel(), user.getStardust(), user.getConsumption()));

        movieListView.getItems().setAll(
                movieDAO.listMovie(Movie.MovieStatus.IN_THEATER));
        loadOrders(user.getUserID());
    }

    private void loadOrders(int userId) {
        orderListView.getItems().clear();
        List<Integer> orderIds = orderDAO.findOrder(userId);
        for (int id : orderIds) {
            Order order = orderDAO.findOrderById(id);
            if (order != null) {
                Movie movie = movieDAOImpl.findMovieById(order.getMovieID());
                String movieName = movie != null ? movie.getMovieName() : "電影#" + order.getMovieID();
                orderListView.getItems().add(String.format("#%d  %s  座位:%s  $%d  [%s]",
                        order.getOrderID(), movieName, order.getSeat(), order.getPrice(), order.getStatus()));
            }
        }
    }

    @FXML
    public void bookTicketAction(ActionEvent event) {
        String selected = movieListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("請先選擇電影");
            return;
        }
        Movie movie = movieDAOImpl.findMovieByName(selected);
        if (movie != null) {
            BookTicketController.setSelectedMovieId(movie.getMovieID());
            AppController.switchScene(event, "/com/theater/book_ticket.fxml", "Theater - 訂票");
        }
    }

    @FXML
    public void loadProductsAction() {
        String selected = movieListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("請先選擇電影以查看周邊");
            return;
        }
        Movie movie = movieDAOImpl.findMovieByName(selected);
        productListView.getItems().clear();
        if (movie != null) {
            for (int pid : productDAO.listProduct(movie.getMovieID())) {
                Product p = productDAO.findProductById(pid);
                if (p != null && p.getStatus() == Product.ProductStatus.SELLING) {
                    productListView.getItems().add(String.format("%s  $%d  庫存:%d",
                            p.getProductName(), p.getPrice(), p.getAmount()));
                }
            }
        }
    }

    @FXML
    public void buyProductAction() {
        int index = productListView.getSelectionModel().getSelectedIndex();
        if (index < 0) {
            statusLabel.setText("請選擇要購買的商品");
            return;
        }
        String selected = movieListView.getSelectionModel().getSelectedItem();
        Movie movie = movieDAOImpl.findMovieByName(selected);
        if (movie == null) return;

        List<Integer> ids = productDAO.listProduct(movie.getMovieID());
        int sellingIndex = 0;
        Product target = null;
        for (int pid : ids) {
            Product p = productDAO.findProductById(pid);
            if (p != null && p.getStatus() == Product.ProductStatus.SELLING) {
                if (sellingIndex == index) {
                    target = p;
                    break;
                }
                sellingIndex++;
            }
        }
        if (target == null || target.getAmount() <= 0) {
            statusLabel.setText("商品已售完");
            return;
        }

        User user = UserSession.getInstance().getCurrentUser();
        Purchase purchase = new Purchase(target.getProductID(), user.getUserID(),
                java.time.LocalDate.now(), target.getPrice());
        PurchaseDAO purchaseDAO = new PurchaseDAOImpl();
        if (purchaseDAO.insertPurchase(purchase)) {
            productDAO.updateAmount(target.getProductID(), -1);
            UserDAO userDAO = new UserDAOImpl();
            userDAO.updateConsumption(user.getUserID(), target.getPrice());
            userDAO.updateStardust(user.getUserID(), target.getPrice() / 10);
            statusLabel.setText("購買成功！");
            loadProductsAction();
            user = userDAO.findUserById(user.getUserID());
            UserSession.getInstance().loginUser(user);
            memberInfoLabel.setText(String.format("等級：%s  |  星塵：%d  |  本月消費：$%d",
                    user.getLevel(), user.getStardust(), user.getConsumption()));
        }
    }

    @FXML
    public void cancelOrderAction() {
        int index = orderListView.getSelectionModel().getSelectedIndex();
        if (index < 0) {
            statusLabel.setText("請選擇要取消的訂單");
            return;
        }
        User user = UserSession.getInstance().getCurrentUser();
        List<Integer> orderIds = orderDAO.findOrder(user.getUserID());
        if (index >= orderIds.size()) return;

        int orderId = orderIds.get(index);
        Order order = orderDAO.findOrderById(orderId);
        if (order == null || order.getStatus() != Order.OrderStatus.VALID) {
            statusLabel.setText("此訂單無法取消");
            return;
        }

        SeatDAO seatDAO = new SeatDAOImpl();
        Seat seat = seatDAO.findSeatByCode(order.getSessionID(), order.getSeat());
        if (orderDAO.cancelOrder(orderId) == 1) {
            if (seat != null) seatDAO.releaseSeat(seat.getSeatID());
            SessionDAO sessionDAO = new SessionDAOImpl();
            sessionDAO.updateSoldSeat(order.getSessionID(), -1);
            statusLabel.setText("訂單已取消");
            loadOrders(user.getUserID());
        }
    }

    @FXML
    public void logoutAction(ActionEvent event) {
        UserSession.getInstance().logout();
        AppController.switchScene(event, "/com/theater/login.fxml", "Theater - 登入");
    }
}
