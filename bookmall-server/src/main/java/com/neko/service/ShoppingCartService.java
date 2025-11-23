package com.neko.service;

import com.neko.dto.ShoppingCartDTO;
import com.neko.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {
    void add(ShoppingCartDTO shoppingCartDTO);

    List<ShoppingCart> show();

    void clean();
}
