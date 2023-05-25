package com.tiagoferreira.library.service.book;

import com.tiagoferreira.library.entity.Book;
import com.tiagoferreira.library.exception.DomainException;
import com.tiagoferreira.library.model.book.BookRequest;
import com.tiagoferreira.library.repository.BookRepository;
import org.springframework.stereotype.Service;

@Service
public class BookService implements IBookService {

    private final BookRepository repository;

    public BookService(BookRepository repository) {
        this.repository = repository;
    }

    @Override
    public Book create(BookRequest request) {

        if (request.getNome() == null) {
            throw new DomainException("Book não pode ser nulo");
        }

        if (isbnIsExists(request.getIsbn())) {
            throw new DomainException("ISBN já cadastrado");
        }


        Book book = new Book();
        book.setNome(request.getNome());
        book.setAutor(request.getAutor());
        book.setIsbn(request.getIsbn());

        return repository.save(book);
    }

    @Override
    public Book getById(Long id) {
        return repository.findById(id).orElseThrow(() -> new DomainException("Book não encontrado"));
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new DomainException("Book não encontrado");
        }
        repository.deleteById(id);
    }

    @Override
    public Book update(BookRequest request, Long id) {
        if (!repository.existsById(id)) {
            throw new DomainException("Book não encontrado");
        }
        var book = repository.findById(id).orElseThrow(() -> new DomainException("Book não encontrado"));
        book.setAutor(request.getAutor());
        book.setNome(request.getNome());
        book.setIsbn(request.getIsbn());
        return repository.save(book);
    }

    private boolean isbnIsExists(String isbn) {
        return repository.existsByIsbn(isbn);
    }
}
