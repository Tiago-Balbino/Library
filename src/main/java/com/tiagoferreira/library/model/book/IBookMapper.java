package com.tiagoferreira.library.model.book;

import com.tiagoferreira.library.entity.Book;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IBookMapper {
    BookResponse toResponse(Book book);
}
