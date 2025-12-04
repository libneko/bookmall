package com.neko.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserPasswordDTO implements Serializable {

    private String email;

    private String password;
}