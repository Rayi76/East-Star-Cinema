package com.theater.model;

import java.util.List;

public interface OrderDAO {
    List<Integer> findOrder(int userID);
    int cancelOrder(int orderID);
    void checkOrderStatus(int orderID);
    void changeOrderStatus(int orderID, Order.OrderStatus status);
    boolean createOrder(Order order);
    Order findOrderById(int orderID);
}
