package com.tiagoferreira.library.unite;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tiagoferreira.library.entity.Book;
import com.tiagoferreira.library.entity.Loan;
import com.tiagoferreira.library.model.loan.LoanRequest;
import com.tiagoferreira.library.model.loan.LoanResponse;
import com.tiagoferreira.library.repository.BookRepository;
import com.tiagoferreira.library.repository.LoanRepository;
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
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Rollback
@Transactional
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LoanControllerTest {

    private final String API = "/loans";

    @Autowired
    BookRepository bookRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mvc;

    @SpyBean
    LoanRepository loanRepository;

    @BeforeEach
    public void setUp() {
        Mockito.reset(loanRepository);
    }

    @Test
    @DisplayName("Criar emprestimo teste bem sucedido")
    public void createWithIsOk() throws Exception {
        var loan = LoanRequest.builder()
                .customer("Fulano")
                .loanDate(LocalDate.now())
                .returned(false)
                .customerEmail("teste@gmail.com")
                .idBook(createBook().getId())
                .build();


        MockHttpServletRequestBuilder request = post(API)
                .contentType("application/json")
                .accept("application/json")
                .content(objectMapper.writeValueAsString(loan));

        ResultActions result = mvc.perform(request);

        final LoanResponse response = objectMapper.readValue(
                result.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8), new TypeReference<>() {
                });

        Assertions.assertEquals(200, result.andReturn().getResponse().getStatus());
        Assertions.assertNotNull(response);
        Assertions.assertEquals(loan.getCustomer(), response.getCustomer());
    }

    @Test
    @DisplayName("Criar emprestimo teste mal sucedido, livro não encontrado")
    public void createWithIsInvalid() throws JsonProcessingException {

        createBook();

        var loan = LoanRequest.builder()
                .customer("Fulano")
                .loanDate(LocalDate.now())
                .returned(false)
                .idBook(null)
                .customerEmail("teste@gmail.com")
                .build();

        MockHttpServletRequestBuilder request = post(API)
                .contentType("application/json")
                .accept("application/json")
                .content(objectMapper.writeValueAsString(loan));

        Assertions.assertThrows(ServletException.class, () -> {
            mvc.perform(request);
        });
    }

    @Test
    @DisplayName("Criar emprestimo teste mal sucedido, livro já emprestado")
    public void createWithIsLoan() throws JsonProcessingException {

        var loanBD = createLoan();

        var loan = LoanRequest.builder()
                .customer("Fulano")
                .loanDate(LocalDate.now())
                .returned(false)
                .idBook(loanBD.getBook().getId())
                .customerEmail("teste@gmail.com")
                .build();

        MockHttpServletRequestBuilder request = post(API)
                .contentType("application/json")
                .accept("application/json")
                .content(objectMapper.writeValueAsString(loan));

        Assertions.assertThrows(ServletException.class, () -> {
            mvc.perform(request);
        });
    }


    @Test
    @DisplayName("Devolver livro teste bem sucedido")
    public void retornedBook() throws Exception {
        var loanBD = createLoan();

        var loan = LoanRequest.builder()
                .returned(true)
                .build();

        MockHttpServletRequestBuilder request = patch(API + "/" + loanBD.getId())
                .contentType("application/json")
                .accept("application/json")
                .content(objectMapper.writeValueAsString(loan));

        ResultActions result = mvc.perform(request);

        final LoanResponse response = objectMapper.readValue(
                result.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8), new TypeReference<>() {
                });

        Assertions.assertEquals(200, result.andReturn().getResponse().getStatus());
        Assertions.assertNotNull(response);
        Assertions.assertEquals(true, response.getReturned());
    }

    @Test
    @DisplayName("Devolver livro teste mal sucedido, Emprestimo não encontrado")
    public void retornedBookIsInvalid() throws JsonProcessingException {
        var loan = LoanRequest.builder()
                .returned(true)
                .build();

        MockHttpServletRequestBuilder request = patch(API + "/1")
                .contentType("application/json")
                .accept("application/json")
                .content(objectMapper.writeValueAsString(loan));

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

    public Loan createLoan() {
        var loan = Loan.builder()
                .customer("Fulano")
                .loanDate(LocalDate.now())
                .returned(false)
                .book(createBook())
                .customerEmail("teste@gmail.com")
                .build();

        return loanRepository.save(loan);
    }

}
