package com.neko.mapper;

import com.neko.entity.BookStock;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BookStockMapper {

    void insert(BookStock bookStock);

    @Delete("delete from book_stock where book_id = #{bookId}")
    void deleteByBookId(Long bookId);

    void deleteByBookIds(List<Long> bookIds);

    @Select("select * from book_stock where book_id = #{bookId}")
    BookStock getByBookId(Long bookId);
}