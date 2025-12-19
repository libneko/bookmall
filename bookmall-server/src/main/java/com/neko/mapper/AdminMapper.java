package com.neko.mapper;

import com.neko.entity.Admin;
import com.neko.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AdminMapper {

    @Select("select * from admin where username = #{username}")
    Admin getByUsername(String username);
}
