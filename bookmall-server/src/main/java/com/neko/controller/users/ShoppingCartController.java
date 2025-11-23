package com.neko.controller.users;

import com.neko.dto.ShoppingCartDTO;
import com.neko.entity.ShoppingCart;
import com.neko.result.Result;
import com.neko.service.ShoppingCartService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/shoppingCart")
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;

    public ShoppingCartController(ShoppingCartService shoppingCartService) {
        this.shoppingCartService = shoppingCartService;
    }

    @PostMapping("/add")
    public Result<Object> add(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        shoppingCartService.add(shoppingCartDTO);
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
}
