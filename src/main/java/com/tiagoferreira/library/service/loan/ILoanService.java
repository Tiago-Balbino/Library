package com.tiagoferreira.library.service.loan;

import com.tiagoferreira.library.entity.Loan;
import com.tiagoferreira.library.model.loan.LoanRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.function.Function;

public interface ILoanService {

    Loan create(LoanRequest request);

    Loan retornedBook(Long id, LoanRequest request);

    void delete(Long id);

    Loan update(Long id, LoanRequest request);

    <S> Page<S> findAll(Pageable pageable, Function<Loan, ? extends S> functionMapper);

    Loan findById(Long id);
}
