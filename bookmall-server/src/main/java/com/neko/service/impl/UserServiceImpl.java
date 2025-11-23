package com.neko.service.impl;

import com.neko.constant.MessageConstant;
import com.neko.constant.PasswordConstant;
import com.neko.dto.UserDTO;
import com.neko.dto.UserLoginDTO;
import com.neko.entity.User;
import com.neko.enums.Status;
import com.neko.exception.AccountLockedException;
import com.neko.exception.AccountNotFoundException;
import com.neko.exception.PasswordErrorException;
import com.neko.mapper.UserMapper;
import com.neko.service.UserService;
import com.neko.utils.PasswordUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public void register(UserDTO userDTO) {
        User user = new User();
        BeanUtils.copyProperties(userDTO,user);

        user.setPassword(PasswordUtil.hashPassword(PasswordConstant.DEFAULT_PASSWORD));
        user.setStatus(Status.ENABLE.getCode());

        userMapper.insert(user);
    }

    @Override
    public User login(UserLoginDTO userLoginDTO) {
        String username = userLoginDTO.getUsername();
        String password = userLoginDTO.getPassword();

        User user = userMapper.getByUsername(username);

        if (user == null) {
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        if (!PasswordUtil.checkPassword(password, user.getPassword())) {
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (user.getStatus().equals(Status.DISABLE.getCode())) {
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        return user;
    }
}
