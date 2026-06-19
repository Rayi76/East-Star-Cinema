package com.theater.model;

import java.util.List;

public interface ProductDAO {
    List<Integer> listProduct(int movieID);
    void updateAmount(int productID, int change);
    void setStatus(int productID, Product.ProductStatus newStatus);
    boolean insertProduct(Product product);
    Product findProductById(int productID);
}
