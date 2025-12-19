package com.neko.service;

import com.neko.dto.UserCodeDTO;
import com.neko.dto.UserDTO;
import com.neko.dto.UserPageQueryDTO;
import com.neko.dto.UserPasswordDTO;
import com.neko.entity.User;
import com.neko.result.PageResult;

public interface UserService {
    User register(UserPasswordDTO userPasswordDTO);

    User login(UserCodeDTO userCodeDTO);

    User login(UserPasswordDTO userPasswordDTO);

    User getProfileById(Long id);

    void updateProfile(UserDTO userDTO);

    PageResult<User> pageQuery(UserPageQueryDTO userPageQueryDTO);
}
