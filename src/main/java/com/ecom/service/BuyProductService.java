package com.ecom.service;

import com.ecom.entity.Cart;
import com.ecom.entity.Product;
import com.ecom.entity.ProductInCart;
import com.ecom.entity.User;
import com.ecom.exception.CannotFoundItemException;

import java.util.List;

public interface BuyProductService {
    Cart addProductInCart(Long productId, int quantity, User user) throws CannotFoundItemException;
    List<ProductInCart> getProductInCartByUser(User user);
}
