package com.theater.model;

import java.time.LocalDate;

public class Product {

    public enum ProductStatus { UNRELEASED, SELLING, SOLD_OUT, PRE_ORDER }

    private int productID;
    private String productName;
    private int movieID;
    private String info;
    private int price;
    private LocalDate publishTime;
    private int amount;
    private ProductStatus status;

    public Product() {
        this.status = ProductStatus.UNRELEASED;
    }

    public Product(String productName, int movieID, String info, int price,
                   LocalDate publishTime, int amount, ProductStatus status) {
        this.productName = productName;
        this.movieID = movieID;
        this.info = info;
        this.price = price;
        this.publishTime = publishTime;
        this.amount = amount;
        this.status = status != null ? status : ProductStatus.UNRELEASED;
    }

    /** 修改商品剩餘數量 */
    public void updateAmount(int productID, int change) {
        if (this.productID == productID) {
            this.amount += change;
            if (amount <= 0) {
                amount = 0;
                status = ProductStatus.SOLD_OUT;
            }
        }
    }

    /** 修改商品上架狀態 */
    public void setStatus(int productID, ProductStatus newStatus) {
        if (this.productID == productID) {
            this.status = newStatus;
        }
    }

    public int getProductID() { return productID; }
    public void setProductID(int productID) { this.productID = productID; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public int getMovieID() { return movieID; }
    public void setMovieID(int movieID) { this.movieID = movieID; }

    public String getInfo() { return info; }
    public void setInfo(String info) { this.info = info; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }

    public LocalDate getPublishTime() { return publishTime; }
    public void setPublishTime(LocalDate publishTime) { this.publishTime = publishTime; }

    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }

    public ProductStatus getStatus() { return status; }
    public void setStatus(ProductStatus status) { this.status = status; }
}
