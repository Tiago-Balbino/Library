package com.tiagoferreira.library.unite;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tiagoferreira.library.entity.Book;
import com.tiagoferreira.library.exception.DomainException;
import com.tiagoferreira.library.model.book.BookRequest;
import com.tiagoferreira.library.model.book.BookResponse;
import com.tiagoferreira.library.repository.BookRepository;
import jakarta.servlet.ServletException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
    @DisplayName("Criar Book teste bem sucesso")
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
    @DisplayName("Criar Book teste mal sucedido")
    public void createWithIsInvalid() throws Exception {

        var book = new BookRequest();

        MockHttpServletRequestBuilder request = post(API).content(objectMapper.writeValueAsString(book)).contentType(MediaType.APPLICATION_JSON);


        Assertions.assertThrows(ServletException.class, () -> {
            mvc.perform(request);
        });
    }

    @Test
    @DisplayName("Criar Book teste mal sucedido, book com ISBN ja cadastrado")
    public void createWithIsISBN() throws Exception {

        var book = createBook();

        MockHttpServletRequestBuilder request = post(API).content(objectMapper.writeValueAsString(book)).contentType(MediaType.APPLICATION_JSON);

        Assertions.assertThrows(ServletException.class, () -> {
            mvc.perform(request);
        });
    }

    @Test
    @DisplayName("Buscar Book por id teste bem sucedido")
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
    @DisplayName("Buscar Book por id teste mal sucedido")
    public void getBookWithIsInvalid() throws Exception {
        MockHttpServletRequestBuilder request = get(API + "/1").contentType(MediaType.APPLICATION_JSON);

        Assertions.assertThrows(ServletException.class, () -> {
            mvc.perform(request);
        });
    }

    @Test
    @DisplayName("Deletar Book por id teste bem sucedido")
    public void deleteBookWithIsOk() throws Exception {

        var book = createBook();

        MockHttpServletRequestBuilder request = delete(API + "/" + book.getId()).contentType(MediaType.APPLICATION_JSON);

        mvc.perform(request);

        Assertions.assertThrows(DomainException.class, () -> {
            bookRepository.findById(book.getId()).orElseThrow(() -> new DomainException("Book não encontrado"));
        });
    }

    @Test
    @DisplayName("Deletar Book por id teste mal sucedido")
    public void deleteBookWithIsInvalid() throws Exception {
        MockHttpServletRequestBuilder request = delete(API + "/1").contentType(MediaType.APPLICATION_JSON);

        Assertions.assertThrows(ServletException.class, () -> {
            mvc.perform(request);
        });
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
