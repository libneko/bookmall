package com.neko.service.impl;

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
import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.search.builder.SearchSourceBuilder;
import org.opensearch.search.sort.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
@Slf4j
public class BookServiceImpl implements BookService {
    private final BookMapper bookMapper;
    private final BookStockMapper bookStockMapper;
    private final RestHighLevelClient restHighLevelClient;

    public BookServiceImpl(BookMapper bookMapper, BookStockMapper bookStockMapper, RestHighLevelClient restHighLevelClient) {
        this.bookMapper = bookMapper;
        this.bookStockMapper = bookStockMapper;
        this.restHighLevelClient = restHighLevelClient;
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
    public PageResult<BookVO> pageQuery(BookPageQueryDTO bookPageQueryDTO) throws IOException {
        int from = (bookPageQueryDTO.getPage() - 1) * bookPageQueryDTO.getPageSize();

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        if (bookPageQueryDTO.getName() != null) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("after.name", bookPageQueryDTO.getName()));
        }

        if (bookPageQueryDTO.getCategoryId() != null) {
            boolQueryBuilder.must(QueryBuilders.termQuery("after.category_id", bookPageQueryDTO.getCategoryId()));
        }

        if (bookPageQueryDTO.getStatus() != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("after.status", bookPageQueryDTO.getStatus()));
        }

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(boolQueryBuilder)
                .from(from)
                .size(bookPageQueryDTO.getPageSize())
                .sort("after.create_time", SortOrder.DESC);

        SearchRequest searchRequest = new SearchRequest("bookmall_postgres.public.book")
                .source(searchSourceBuilder);

        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        List<BookVO> books = Arrays.stream(response.getHits().getHits())
                .map(hit -> {
                    Map<String, Object> after = (Map<String, Object>) hit.getSourceAsMap().get("after");
                    BookVO vo = new BookVO();
                    vo.setId(((Number) after.get("id")).longValue());
                    vo.setName((String) after.get("name"));
                    vo.setAuthor((String) after.get("author"));
                    vo.setPrice(new BigDecimal(new BigInteger(Base64.getDecoder().decode(after.get("price").toString())), 2));
                    vo.setImage((String) after.get("image"));
                    vo.setCategoryId(((Number) after.get("category_id")).longValue());
                    vo.setStatus((Integer) after.get("status"));
                    vo.setUpdateTime(LocalDateTime.ofInstant(
                            Instant.ofEpochMilli(((Number) after.get("update_time")).longValue()),
                            ZoneId.systemDefault()
                    ));
                    vo.setDescription((String) after.get("description"));
                    return vo;
                }).toList();

        long total = response.getHits().getTotalHits().value();
        return new PageResult<>(total, books);
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

    @Override
    public List<BookVO> randomList() {
        List<Book> books = bookMapper.randomList();

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
