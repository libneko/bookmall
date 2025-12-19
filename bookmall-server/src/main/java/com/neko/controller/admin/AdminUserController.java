package com.neko.controller.admin;

import com.neko.dto.UserPageQueryDTO;
import com.neko.entity.User;
import com.neko.result.PageResult;
import com.neko.result.Result;
import com.neko.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/user")
@Slf4j
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/page")
    public Result<PageResult<User>> page(UserPageQueryDTO userPageQueryDTO) {
        log.info("Page Query user: {}", userPageQueryDTO);
        PageResult<User> pageResult = userService.pageQuery(userPageQueryDTO);
        return Result.success(pageResult);
    }
}
