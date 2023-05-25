package com.tiagoferreira.library.controller;

import com.tiagoferreira.library.model.book.BookMapper;
import com.tiagoferreira.library.model.book.BookRequest;
import com.tiagoferreira.library.model.book.BookResponse;
import com.tiagoferreira.library.service.book.IBookService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookMapper mapper;
    private final IBookService service;

    public BookController(BookMapper mapper, IBookService service) {
        this.mapper = mapper;
        this.service = service;
    }

    @PostMapping
    public BookResponse create(@RequestBody BookRequest request) {
        return mapper.toResponse(service.create(request));
    }

    @GetMapping("/{id}")
    public BookResponse getById(@PathVariable Long id) {
        return mapper.toResponse(service.getById(id));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @PutMapping("/{id}")
    public BookResponse update(@RequestBody BookRequest request, @PathVariable Long id) {
        return mapper.toResponse(service.update(request, id));
    }

    @GetMapping("/all")
    public Page<BookResponse> findAll(Pageable pageable) {
        return service.findAll(pageable, mapper::toResponse);
    }
}
