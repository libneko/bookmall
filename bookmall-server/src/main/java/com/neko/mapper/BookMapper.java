package com.neko.mapper;

import com.neko.entity.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BookMapper {

    @Select("select count(id) from book where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    @Select("select * from book where id = #{id}")
    Book getById(Long id);

    List<Book> list(Book dish);
}
