package com.neko.controller.users;

import com.neko.entity.Book;
import com.neko.enums.Status;
import com.neko.result.Result;
import com.neko.service.BookService;
import com.neko.vo.BookVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user/book")
@Slf4j
public class UserBookController {

    private final RedisTemplate<String, Object> redisTemplate;
    private final BookService bookService;

    public UserBookController(RedisTemplate<String, Object> redisTemplate, BookService bookService) {
        this.redisTemplate = redisTemplate;
        this.bookService = bookService;
    }

    @GetMapping("/list")
    public Result<List<BookVO>> list(Long categoryId){
        String key = "book_" + categoryId;
        List<BookVO> list = (List<BookVO>) redisTemplate.opsForValue().get(key);

        if (list != null & !list.isEmpty()) {
            return Result.success(list);
        }

        Book book = new Book();
        book.setCategoryId(categoryId);
        book.setStatus(Status.ENABLE.getCode());

        list = bookService.list(book);
        redisTemplate.opsForValue().set(key, list);

        return Result.success(list);
    }
}
