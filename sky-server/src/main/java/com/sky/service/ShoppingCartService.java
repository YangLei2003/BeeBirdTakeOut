package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {
    void addShop(ShoppingCartDTO shoppingCartDTO);

    List<ShoppingCart> lookCart();

    void clearCart();
}
