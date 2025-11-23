package com.neko.service.impl;

import com.neko.dto.BookDTO;
import com.neko.dto.BookPageQueryDTO;
import com.neko.entity.Book;
import com.neko.enums.Status;
import com.neko.mapper.BookMapper;
import com.neko.result.PageResult;
import com.neko.service.BookService;
import com.neko.vo.BookVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class BookServiceImpl implements BookService {
    private final BookMapper bookMapper;

    public BookServiceImpl(BookMapper bookMapper) {
        this.bookMapper = bookMapper;
    }

    @Override
    public void save(BookDTO bookDTO) {

    }

    @Override
    public PageResult<BookVO> pageQuery(BookPageQueryDTO bookPageQueryDTO) {
        return null;
    }

    @Override
    public void deleteBatch(List<Long> ids) {

    }

    @Override
    public BookVO getById(Long id) {
        return null;
    }

    @Override
    public void update(BookDTO bookDTO) {

    }

    @Override
    public void setStatus(Integer status, Long id) {

    }

    @Override
    public List<Book> list(Long categoryId) {
        Book book = Book.builder()
                .categoryId(categoryId)
                .status(Status.ENABLE.getCode())
                .build();
        return bookMapper.list(book);
    }

    @Override
    public List<BookVO> list(Book book) {
        List<Book> books = bookMapper.list(book);

        List<BookVO> bookVOList = new ArrayList<>();

        for (Book b : books) {
            BookVO bookVO = new BookVO();
            BeanUtils.copyProperties(b, bookVO);

            bookVOList.add(bookVO);
        }

        return bookVOList;
    }
}
