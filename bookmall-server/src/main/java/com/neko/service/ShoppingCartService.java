package com.neko.service;

import com.neko.dto.ShoppingCartDTO;
import com.neko.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {
    void addOrUpdate(ShoppingCartDTO shoppingCartDTO, boolean isAdd);

    List<ShoppingCart> show();

    void clean();

    void delete(ShoppingCart shoppingCart);
}
