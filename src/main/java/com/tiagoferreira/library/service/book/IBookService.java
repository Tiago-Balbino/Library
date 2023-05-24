package com.tiagoferreira.library.service.book;

import com.tiagoferreira.library.entity.Book;
import com.tiagoferreira.library.model.book.BookRequest;

public interface IBookService {
    Book create(BookRequest request);

    Book getById(Long id);

    void delete(Long id);
}
