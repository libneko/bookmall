package com.neko.controller;

import com.neko.constant.JwtClaimsConstant;
import com.neko.dto.UserDTO;
import com.neko.dto.UserLoginDTO;
import com.neko.entity.User;
import com.neko.properties.JwtProperties;
import com.neko.result.Result;
import com.neko.service.UserService;
import com.neko.utils.JwtUtil;
import com.neko.vo.UserLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserService userService;

    private final JwtProperties jwtProperties;

    public UserController(UserService userService, JwtProperties jwtProperties) {
        this.userService = userService;
        this.jwtProperties = jwtProperties;
    }

    @PostMapping("/register")
    public Result<String> register(@RequestBody UserDTO userDTO){
        log.info("User register: {}", userDTO);
        userService.register(userDTO);
        return Result.success();
    }

    @PostMapping("/login")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO) {
        log.info("User login: {}", userLoginDTO);

        User user = userService.login(userLoginDTO);

        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, user.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .token(token)
                .build();

        return Result.success(userLoginVO);
    }
}
