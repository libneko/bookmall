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
import com.neko.repository.UserRepository;
import com.neko.service.UserService;
import com.neko.utils.PasswordUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void register(UserDTO userDTO) {
        User user = new User();
        BeanUtils.copyProperties(userDTO,user);

        user.setPassword(PasswordUtil.hashPassword(PasswordConstant.DEFAULT_PASSWORD));
        user.setStatus(Status.ENABLE.getCode());

        userRepository.save(user);
    }

    @Override
    public User login(UserLoginDTO userLoginDTO) {
        String username = userLoginDTO.getUsername();
        String password = userLoginDTO.getPassword();

        Optional<User> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isEmpty()) {
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        User user = optionalUser.get();
        if (!PasswordUtil.checkPassword(password, user.getPassword())) {
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (user.getStatus().equals(Status.DISABLE.getCode())) {
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        return user;
    }
}
