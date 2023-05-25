package com.tiagoferreira.library.unite;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tiagoferreira.library.entity.Book;
import com.tiagoferreira.library.entity.Loan;
import com.tiagoferreira.library.model.book.BookResponse;
import com.tiagoferreira.library.model.loan.LoanRequest;
import com.tiagoferreira.library.model.loan.LoanResponse;
import com.tiagoferreira.library.repository.BookRepository;
import com.tiagoferreira.library.repository.LoanRepository;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

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
                .contentType(MediaType.APPLICATION_JSON)
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
                .contentType(MediaType.APPLICATION_JSON)
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
                .contentType(MediaType.APPLICATION_JSON)
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
                .contentType(MediaType.APPLICATION_JSON)
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
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loan));

        Assertions.assertThrows(ServletException.class, () -> {
            mvc.perform(request);
        });
    }


    @Test
    @DisplayName("Deletar emprestimo teste bem sucedido")
    public void deleteLoanIsOk() {
        var loanBD = createLoan();

        MockHttpServletRequestBuilder request = delete(API + "/" + loanBD.getId())
                .contentType(MediaType.APPLICATION_JSON);

        Assertions.assertDoesNotThrow(() -> {
            mvc.perform(request);
        });

    }

    @Test
    @DisplayName("Deletar emprestimo teste mal sucedido, Emprestimo não encontrado")
    public void deleteLoanIsInvalid() {
        createLoan();

        MockHttpServletRequestBuilder request = delete(API + "/1")
                .contentType(MediaType.APPLICATION_JSON);

        Assertions.assertThrows(ServletException.class, () -> {
            mvc.perform(request);
        });
    }

    @Test
    @DisplayName("Atualizar emprestimo teste bem sucedido")
    public void updateLoanIsOk() throws Exception {
        var loanBD = createLoan();

        var loan = LoanRequest.builder()
                .customer("Fulano 2")
                .loanDate(LocalDate.now())
                .returned(false)
                .customerEmail(loanBD.getCustomerEmail())
                .idBook(loanBD.getBook().getId())
                .build();

        MockHttpServletRequestBuilder request = put(API + "/" + loanBD.getId())
                .contentType(MediaType.APPLICATION_JSON)
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
    @DisplayName("Atualizar emprestimo teste mal sucedido, Emprestimo não encontrado")
    public void updateLoanIsInvalid() throws JsonProcessingException {
        var loanDB = createLoan();

        var loan = LoanRequest.builder()
                .customer("Fulano 2")
                .loanDate(LocalDate.now())
                .returned(false)
                .customerEmail("Teste@gmail.com")
                .idBook(null)
                .build();

        MockHttpServletRequestBuilder request = put(API + "/" + (loanDB.getId() + 1L))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loan));

        Assertions.assertThrows(ServletException.class, () -> {
            mvc.perform(request);
        });
    }

    @Rollback
    @ParameterizedTest
    @CsvSource({"0, 5, 20, 5", "2, 5, 20, 5", "10, 5, 20, 0"})
    @DisplayName("Buscar todos os livros paginado teste bem sucedido")
    public void findAllLoanWithIsOk(int page, int size, int fakeSize, int totalExpected) throws Exception {

        List<Loan> emprestimos = new ArrayList<>();

        for (int i = 1; i <= fakeSize; i++) {
            emprestimos.add(createLoanForList((long) i));
        }

        Assertions.assertEquals(fakeSize, emprestimos.size());

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

        List<Loan> emprestimos = new ArrayList<>();

        for (int i = 1; i <= fakeSize; i++) {
            emprestimos.add(createLoanForList((long) i));
        }

        Assertions.assertEquals(fakeSize, emprestimos.size());

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

    @Test
    @DisplayName("Buscar emprestimo por id teste bem sucedido")
    public void findByIdIsOk() throws Exception {
        var loanBD = createLoan();

        MockHttpServletRequestBuilder request = get(API + "/" + loanBD.getId())
                .contentType(MediaType.APPLICATION_JSON);

        ResultActions result = mvc.perform(request);

        final LoanResponse response = objectMapper.readValue(
                result.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8), new TypeReference<>() {
                });

        Assertions.assertEquals(200, result.andReturn().getResponse().getStatus());
        Assertions.assertNotNull(response);
        Assertions.assertEquals(loanBD.getCustomer(), response.getCustomer());
    }

    @Test
    @DisplayName("Buscar emprestimo por id teste mal sucedido, Emprestimo não encontrado")
    public void findByIdIsInvalid() throws Exception {
        var loanBD = createLoan();

        MockHttpServletRequestBuilder request = get(API + "/" + (loanBD.getId() + 1L))
                .contentType(MediaType.APPLICATION_JSON);

        Assertions.assertThrows(ServletException.class, () -> {
            mvc.perform(request);
        });
    }

    private Loan createLoanForList(Long id) {
        var loan = Loan.builder()
                .id(id)
                .customer("Fulano " + id)
                .loanDate(LocalDate.now())
                .returned(false)
                .book(createBook())
                .customerEmail("teste@gmail.com")
                .build();

        return loanRepository.save(loan);
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
