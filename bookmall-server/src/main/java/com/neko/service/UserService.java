package com.neko.service;

import com.neko.dto.UserDTO;
import com.neko.dto.UserLoginDTO;
import com.neko.entity.User;

public interface UserService {
    void register(UserDTO userDTO);

    User login(UserLoginDTO userLoginDTO);
}
