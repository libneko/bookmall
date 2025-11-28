package com.neko.service.impl;

import com.neko.context.BaseContext;
import com.neko.dto.ShoppingCartDTO;
import com.neko.entity.Book;
import com.neko.entity.ShoppingCart;
import com.neko.mapper.BookMapper;
import com.neko.mapper.ShoppingCartMapper;
import com.neko.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final ShoppingCartMapper shoppingCartMapper;
    private final BookMapper bookMapper;

    public ShoppingCartServiceImpl(ShoppingCartMapper shoppingCartMapper, BookMapper bookMapper) {
        this.shoppingCartMapper = shoppingCartMapper;
        this.bookMapper = bookMapper;
    }

    @Override
    public void add(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);

        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);

        if (list != null && !list.isEmpty()) {
            ShoppingCart cart = list.getFirst();
            cart.setNumber(cart.getNumber() + 1);
            shoppingCartMapper.updateNumberById(cart);
        } else {
            Long bookId = shoppingCartDTO.getBookId();
            log.info("book id: {}", bookId);
            Book book = bookMapper.getById(bookId);
            shoppingCart.setName(book.getName());
            shoppingCart.setImage(book.getImage());
            shoppingCart.setAmount(book.getPrice());
        }
        shoppingCart.setNumber(1);
        shoppingCart.setCreateTime(LocalDateTime.now());

        shoppingCartMapper.insert(shoppingCart);
    }

    @Override
    public List<ShoppingCart> show() {
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(userId);
        return shoppingCartMapper.list(shoppingCart);
    }

    @Override
    public void clean() {
        Long userId = BaseContext.getCurrentId();
        shoppingCartMapper.deleteByUserId(userId);
    }
}
