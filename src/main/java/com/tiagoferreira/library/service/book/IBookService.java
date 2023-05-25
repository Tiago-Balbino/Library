package com.tiagoferreira.library.service.book;

import com.tiagoferreira.library.entity.Book;
import com.tiagoferreira.library.model.book.BookRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.function.Function;

public interface IBookService {

    Book create(BookRequest request);

    Book getById(Long id);

    void delete(Long id);

    Book update(BookRequest request, Long id);

    <S> Page<S> findAll(Pageable pageable, Function<Book, ? extends S> functionMapper);
}
