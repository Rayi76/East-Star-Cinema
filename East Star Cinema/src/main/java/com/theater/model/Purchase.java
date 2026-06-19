package com.theater.model;

import java.time.LocalDate;

public class Purchase {

    private int purchaseID;
    private int productID;
    private int userID;
    private LocalDate orderTime;
    private String type;
    private int soldPrice;

    public Purchase() {
        this.orderTime = LocalDate.now();
    }

    /** 使用者購買商品時建構 Purchase 物件 */
    public Purchase(int productID, int userID, LocalDate orderTime, int soldPrice) {
        this.productID = productID;
        this.userID = userID;
        this.orderTime = orderTime != null ? orderTime : LocalDate.now();
        this.soldPrice = soldPrice;
        this.type = "NORMAL";
    }

    public Purchase(int productID, int userID, LocalDate orderTime, String type, int soldPrice) {
        this(productID, userID, orderTime, soldPrice);
        this.type = type;
    }

    /** 取消商品訂單 */
    public void cancellPurchase(int purchaseID) {
        if (this.purchaseID == purchaseID) {
            System.out.println("商品訂單 " + purchaseID + " 已取消");
        }
    }

    public int getPurchaseID() { return purchaseID; }
    public void setPurchaseID(int purchaseID) { this.purchaseID = purchaseID; }

    public int getProductID() { return productID; }
    public void setProductID(int productID) { this.productID = productID; }

    public int getUserID() { return userID; }
    public void setUserID(int userID) { this.userID = userID; }

    public LocalDate getOrderTime() { return orderTime; }
    public void setOrderTime(LocalDate orderTime) { this.orderTime = orderTime; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getSoldPrice() { return soldPrice; }
    public void setSoldPrice(int soldPrice) { this.soldPrice = soldPrice; }
}
