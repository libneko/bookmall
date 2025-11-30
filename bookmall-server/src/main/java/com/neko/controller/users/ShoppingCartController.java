package com.neko.controller.users;

import com.neko.dto.ShoppingCartDTO;
import com.neko.entity.ShoppingCart;
import com.neko.result.Result;
import com.neko.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/shoppingCart")
@Slf4j
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;

    public ShoppingCartController(ShoppingCartService shoppingCartService) {
        this.shoppingCartService = shoppingCartService;
    }

    @PostMapping("/add")
    public Result<Object> add(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        log.info("Add shopping cart, item info: {}", shoppingCartDTO);
        shoppingCartService.addOrUpdate(shoppingCartDTO, true);
        return Result.success();
    }

    @GetMapping("/list")
    public Result<List<ShoppingCart>> list() {
        List<ShoppingCart> list = shoppingCartService.show();
        return Result.success(list);
    }

    @DeleteMapping("/clean")
    public Result<Object> clean() {
        shoppingCartService.clean();
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Object> deleteById(@PathVariable Integer id) {
        shoppingCartService.deleteById(id);
        return Result.success();
    }

    @PutMapping("/update")
    public Result<Object> update(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        log.info("Update shopping cart, item info: {}", shoppingCartDTO);
        shoppingCartService.addOrUpdate(shoppingCartDTO, false);
        return Result.success();
    }
}
