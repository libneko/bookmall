package com.neko.service.impl;

import com.neko.constant.MessageConstant;
import com.neko.dto.UserCodeDTO;
import com.neko.dto.UserDTO;
import com.neko.dto.UserPasswordDTO;
import com.neko.entity.User;
import com.neko.enums.Status;
import com.neko.exception.AccountLockedException;
import com.neko.exception.AccountNotFoundException;
import com.neko.exception.PasswordErrorException;
import com.neko.mapper.UserMapper;
import com.neko.service.UserService;
import com.neko.utils.CodeUtil;
import com.neko.utils.PasswordUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    private final String avatar = "https://neko-book.oss-cn-hangzhou.aliyuncs.com/default_avatar.jpg";

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public User register(UserPasswordDTO userPasswordDTO) {
        User user = new User();
        BeanUtils.copyProperties(userPasswordDTO, user);

        user.setPassword(PasswordUtil.hashPassword(user.getPassword()));
        user.setAvatar(avatar);
        user.setStatus(Status.ENABLE.getCode());

        userMapper.insert(user);
        return user;
    }

    @Override
    @Transactional
    public User login(UserCodeDTO userCodeDTO) {
        String email = userCodeDTO.getEmail();

        User user = userMapper.getByEmail(email);

        if (user == null) {
            // 用户不存在，自动注册用户
            user = new User();
            user.setUsername("小书架用户_" + CodeUtil.generate(8));
            user.setEmail(email);
            user.setAvatar(avatar);
            user.setStatus(Status.ENABLE.getCode());
            userMapper.insert(user);
        }

        user = userMapper.getByEmail(email);

        return user;
    }

    @Override
    public User login(UserPasswordDTO userPasswordDTO) {
        String email = userPasswordDTO.getEmail();
        String password = userPasswordDTO.getPassword();

        User user = userMapper.getByEmail(email);

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

    @Override
    public User getProfileById(Long id) {
        return userMapper.getById(id);
    }

    @Override
    public void updateProfile(UserDTO userDTO) {
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);

        userMapper.update(user);
    }
}
