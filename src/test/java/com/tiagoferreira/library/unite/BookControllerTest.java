package com.tiagoferreira.library.unite;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tiagoferreira.library.entity.Book;
import com.tiagoferreira.library.exception.DomainException;
import com.tiagoferreira.library.model.book.BookRequest;
import com.tiagoferreira.library.model.book.BookResponse;
import com.tiagoferreira.library.repository.BookRepository;
import com.tiagoferreira.library.suport.PaginationImpl;
import jakarta.servlet.ServletException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@Rollback
@Transactional
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookControllerTest {
    private final String API = "/books";

    @Autowired
    MockMvc mvc;

    @SpyBean
    BookRepository bookRepository;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        Mockito.reset(bookRepository);
    }

    @Test
    @DisplayName("Criar livro teste bem sucesso")
    public void createWithIsOk() throws Exception {
        var book = BookRequest.builder()
                .nome("Harry Potter")
                .autor("J.K.Rolling")
                .isbn("adsa")
                .build();

        MockHttpServletRequestBuilder request = post(API).content(objectMapper.writeValueAsString(book)).contentType(MediaType.APPLICATION_JSON);

        ResultActions result = mvc.perform(request);

        final BookResponse response = objectMapper.readValue(result.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8), new TypeReference<>() {
        });

        Assertions.assertNotNull(response);
        Assertions.assertEquals(book.getNome(), response.getNome());
    }

    @Test
    @DisplayName("Criar livro teste mal sucedido")
    public void createWithIsInvalid() throws Exception {

        var book = new BookRequest();

        MockHttpServletRequestBuilder request = post(API).content(objectMapper.writeValueAsString(book)).contentType(MediaType.APPLICATION_JSON);


        Assertions.assertThrows(ServletException.class, () -> {
            mvc.perform(request);
        });
    }

    @Test
    @DisplayName("Criar livro teste mal sucedido, book com ISBN ja cadastrado")
    public void createWithIsISBN() throws Exception {

        var book = createBook();

        MockHttpServletRequestBuilder request = post(API).content(objectMapper.writeValueAsString(book)).contentType(MediaType.APPLICATION_JSON);

        Assertions.assertThrows(ServletException.class, () -> {
            mvc.perform(request);
        });
    }

    @Test
    @DisplayName("Buscar livro por id teste bem sucedido")
    public void getBookWithIsOk() throws Exception {
        var book = createBook();

        MockHttpServletRequestBuilder request = get(API + "/" + book.getId()).contentType(MediaType.APPLICATION_JSON);

        ResultActions result = mvc.perform(request);

        final BookResponse response = objectMapper.readValue(result.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8), new TypeReference<>() {
        });

        Assertions.assertNotNull(response);
        Assertions.assertEquals(book.getNome(), response.getNome());
        Assertions.assertEquals(book.getId(), response.getId());
    }

    @Test
    @DisplayName("Buscar livro por id teste mal sucedido")
    public void getBookWithIsInvalid() {
        MockHttpServletRequestBuilder request = get(API + "/1").contentType(MediaType.APPLICATION_JSON);

        Assertions.assertThrows(ServletException.class, () -> {
            mvc.perform(request);
        });
    }

    @Test
    @DisplayName("Deletar livro por id teste bem sucedido")
    public void deleteBookWithIsOk() throws Exception {

        var book = createBook();

        MockHttpServletRequestBuilder request = delete(API + "/" + book.getId()).contentType(MediaType.APPLICATION_JSON);

        mvc.perform(request);

        Assertions.assertThrows(DomainException.class, () -> {
            bookRepository.findById(book.getId()).orElseThrow(() -> new DomainException("Livro não encontrado"));
        });
    }

    @Test
    @DisplayName("Deletar livro por id teste mal sucedido")
    public void deleteBookWithIsInvalid() throws Exception {
        MockHttpServletRequestBuilder request = delete(API + "/1").contentType(MediaType.APPLICATION_JSON);

        Assertions.assertThrows(ServletException.class, () -> {
            mvc.perform(request);
        });
    }

    @Test
    @DisplayName("Atualizar livro teste bem sucedido")
    public void updateBookWithIsOk() throws Exception {

        var book = createBook();

        var bookUpdate = BookRequest.builder()
                .nome("Harry Potter 2")
                .autor(book.getAutor())
                .isbn(book.getIsbn())
                .build();

        MockHttpServletRequestBuilder request = put(API + "/" + book.getId())
                .content(objectMapper.writeValueAsString(bookUpdate)).contentType(MediaType.APPLICATION_JSON);

        ResultActions result = mvc.perform(request);

        final BookResponse response = objectMapper.readValue(
                result.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8), new TypeReference<>() {
                });

        Assertions.assertEquals(200, result.andReturn().getResponse().getStatus());
        Assertions.assertNotNull(response);
        Assertions.assertEquals(bookUpdate.getNome(), response.getNome());

    }

    @Test
    @DisplayName("Atualizar Book teste mal sucedido")
    public void updateBookWithIsInvalid() throws Exception {
        var book = createBook();

        var bookUpdate = BookRequest.builder()
                .nome("Harry Potter 2")
                .autor(book.getAutor())
                .isbn(book.getIsbn())
                .build();

        MockHttpServletRequestBuilder request = put(API + "/2")
                .content(objectMapper.writeValueAsString(bookUpdate)).contentType(MediaType.APPLICATION_JSON);

        Assertions.assertThrows(ServletException.class, () -> {
            mvc.perform(request);
        });

    }

    @Rollback
    @ParameterizedTest
    @CsvSource({"0, 5, 20, 5", "2, 5, 20, 5", "10, 5, 20, 0"})
    @DisplayName("Buscar todos os livros paginado teste bem sucedido")
    public void findAllBookWithIsOk(int page, int size, int fakeSize, int totalExpected) throws Exception {

        List<Book> livros = new ArrayList<>();

        for (int i = 1; i <= fakeSize; i++) {
            livros.add(createBookForList((long) i));
        }

        Assertions.assertEquals(fakeSize, livros.size());

        final MockHttpServletRequestBuilder requestBuilder = get(API + "/all")
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size))
                .param("order", "id,asc")
                .contentType(MediaType.APPLICATION_JSON);

        final ResultActions result = mvc.perform(requestBuilder);

        final PaginationImpl<BookResponse> response = objectMapper.readValue(result.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8), new TypeReference<>() {
        });

        Assertions.assertNotNull(response);
        Assertions.assertEquals(response.getContent().size(), totalExpected);
    }

    @Rollback
    @ParameterizedTest
    @CsvSource({"0, 5, 20, 10", "2, 5, 20, 10", "10, 5, 20, 10"})
    @DisplayName("Buscar todos os livros paginado teste mal sucedido")
    public void findAllBookWithIsInvalid(int page, int size, int fakeSize, int totalExpected) throws Exception {

        List<Book> livros = new ArrayList<>();

        for (int i = 1; i <= fakeSize; i++) {
            livros.add(createBookForList((long) i));
        }

        Assertions.assertEquals(fakeSize, livros.size());

        final MockHttpServletRequestBuilder requestBuilder = get(API + "/all")
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size))
                .param("order", "id,asc")
                .contentType(MediaType.APPLICATION_JSON);

        final ResultActions result = mvc.perform(requestBuilder);

        final PaginationImpl<BookResponse> response = objectMapper.readValue(result.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8), new TypeReference<>() {
        });

        Assertions.assertNotNull(response);
        Assertions.assertNotEquals(response.getContent().size(), totalExpected);
    }

    private Book createBookForList(Long id) {
        var book = Book.builder()
                .id(id)
                .nome("Harry Potter " + id)
                .autor("J.K.Rolling")
                .isbn("adsa")
                .build();

        return bookRepository.save(book);
    }

    public Book createBook() {
        var book = Book.builder()
                .nome("Harry Potter")
                .autor("J.K.Rolling")
                .isbn("adsa")
                .build();

        return bookRepository.save(book);
    }
}
