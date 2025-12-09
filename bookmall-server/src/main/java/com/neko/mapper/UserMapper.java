package com.neko.mapper;

import com.neko.annotation.AutoFill;
import com.neko.entity.User;
import com.neko.enums.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    @Select("select * from users where email = #{email}")
    User getByEmail(String email);

    @Insert("insert into users (username, email, password, phone, sex, id_number, create_time, update_time, status)"
            +
            "values " +
            "(#{username}, #{email}, #{password}, #{phone}, #{sex}, #{idNumber}, #{createTime}, #{updateTime}, #{status})")
    @AutoFill(value = OperationType.INSERT)
    void insert(User user);

    @Select("select * from users where id = #{id}")
    User getById(Long id);

    @AutoFill(value = OperationType.UPDATE)
    void update(User user);
}