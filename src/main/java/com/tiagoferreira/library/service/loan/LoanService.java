package com.tiagoferreira.library.service.loan;

import com.tiagoferreira.library.entity.Loan;
import com.tiagoferreira.library.exception.DomainException;
import com.tiagoferreira.library.model.loan.LoanRequest;
import com.tiagoferreira.library.repository.LoanRepository;
import com.tiagoferreira.library.service.book.IBookService;
import org.springframework.stereotype.Service;

@Service
public class LoanService implements ILoanService {

    private final LoanRepository repository;

    private final IBookService bookService;

    public LoanService(LoanRepository repository, IBookService bookService) {
        this.repository = repository;
        this.bookService = bookService;
    }

    @Override
    public Loan create(LoanRequest request) {

        if (repository.existsByBookAndNotReturned(request.getIdBook())) {
            throw new DomainException("Livro Já Emprestado");
        }

        var book = bookService.getById(request.getIdBook());
        Loan loan = new Loan();
        loan.setBook(book);
        loan.setCustomer(request.getCustomer());
        loan.setLoanDate(request.getLoanDate());
        loan.setReturned(request.getReturned());
        return repository.save(loan);
    }

    @Override
    public Loan retornedBook(Long id, LoanRequest request) {
        var loan = repository.findById(id).orElseThrow(() -> new DomainException("Empréstimo não encontrado"));
        loan.setReturned(request.getReturned());
        return repository.save(loan);
    }
}
