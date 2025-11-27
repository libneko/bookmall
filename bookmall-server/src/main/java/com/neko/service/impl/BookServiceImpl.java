package com.neko.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.neko.constant.MessageConstant;
import com.neko.dto.BookDTO;
import com.neko.dto.BookPageQueryDTO;
import com.neko.entity.Book;
import com.neko.entity.BookStock;
import com.neko.enums.Status;
import com.neko.exception.DeletionNotAllowedException;
import com.neko.mapper.BookMapper;
import com.neko.mapper.BookStockMapper;
import com.neko.result.PageResult;
import com.neko.service.BookService;
import com.neko.vo.BookVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class BookServiceImpl implements BookService {
    private final BookMapper bookMapper;
    private final BookStockMapper bookStockMapper;

    public BookServiceImpl(BookMapper bookMapper, BookStockMapper bookStockMapper) {
        this.bookMapper = bookMapper;
        this.bookStockMapper = bookStockMapper;
    }

    @Override
    @Transactional
    public void save(BookDTO bookDTO) {
        Book book = new Book();
        BeanUtils.copyProperties(bookDTO, book);

        bookMapper.insert(book);

        Long bookId = book.getId();

        BookStock bookStock = bookDTO.getStock();
        if (Objects.nonNull(bookStock)) {
            bookStock.setBookId(bookId);
            bookStockMapper.insert(bookStock);
        }
    }

    @Override
    public PageResult<BookVO> pageQuery(BookPageQueryDTO bookPageQueryDTO) {
        PageHelper.startPage(bookPageQueryDTO.getPage(), bookPageQueryDTO.getPageSize());
        Page<BookVO> page = bookMapper.pageQuery(bookPageQueryDTO);
        return new PageResult<>(page.getTotal(), page.getResult());
    }

    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        List<Book> books = bookMapper.getByIds(ids);
        if (books.stream().anyMatch(book -> Objects.equals(book.getStatus(), Status.ENABLE.getCode()))) {
            throw new DeletionNotAllowedException(MessageConstant.BOOK_ON_SALE);
        }

        bookMapper.deleteByIds(ids);
        bookStockMapper.deleteByBookIds(ids);
    }

    @Override
    public BookVO getById(Long id) {
        Book book = bookMapper.getById(id);

        BookStock bookStock = bookStockMapper.getByBookId(id);

        BookVO bookVO = new BookVO();
        BeanUtils.copyProperties(book, bookVO);
        bookVO.setBookStock(bookStock);

        return bookVO;
    }

    @Override
    public void update(BookDTO bookDTO) {
        Book book = new Book();
        BeanUtils.copyProperties(bookDTO, book);

        bookMapper.update(book);

        bookStockMapper.deleteByBookId(book.getId());

        BookStock bookStock = bookDTO.getStock();
        if (Objects.nonNull(bookStock)) {
            bookStock.setBookId(book.getId());
            bookStockMapper.insert(bookStock);
        }
    }

    @Override
    public void setStatus(Integer status, Long id) {
        Book book = Book.builder()
                .id(id)
                .status(status)
                .build();
        bookMapper.update(book);
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

            BookStock bookStock = bookStockMapper.getByBookId(b.getId());

            bookVO.setBookStock(bookStock);
            bookVOList.add(bookVO);
        }

        return bookVOList;
    }
}
