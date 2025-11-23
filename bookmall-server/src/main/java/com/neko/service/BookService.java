package com.neko.service;

import com.neko.dto.BookDTO;
import com.neko.dto.BookPageQueryDTO;
import com.neko.entity.Book;
import com.neko.result.PageResult;
import com.neko.vo.BookVO;

import java.util.List;

public interface BookService {
    void save(BookDTO bookDTO);

    PageResult<BookVO> pageQuery(BookPageQueryDTO bookPageQueryDTO);

    void deleteBatch(List<Long> ids);

    BookVO getById(Long id);

    void update(BookDTO bookDTO);

    void setStatus(Integer status, Long id);

    List<Book> list(Long categoryId);

    List<BookVO> list(Book book);
}
