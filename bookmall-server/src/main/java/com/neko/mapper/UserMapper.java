package com.neko.mapper;

import com.neko.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    /**
     * 根据用户名查询员工
     *
     * @param username
     * @return
     */
    @Select("select * from users where username = #{username}")
    User getByUsername(String username);

    @Insert("insert into users (name, username, password, phone, sex, id_number, create_time, update_time, create_user, update_user, status)"
            +
            "values " +
            "(#{name}, #{username}, #{password}, #{phone}, #{sex}, #{idNumber}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser}, #{status})")
    void insert(User user);
}