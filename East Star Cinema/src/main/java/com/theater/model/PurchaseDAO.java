package com.theater.model;

import java.util.List;

public interface PurchaseDAO {
    List<Integer> listPurchase(int userID);
    void cancelPurchase(int purchaseID);
    boolean insertPurchase(Purchase purchase);
    Purchase findPurchaseById(int purchaseID);
}
