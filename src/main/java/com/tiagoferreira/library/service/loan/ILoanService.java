package com.tiagoferreira.library.service.loan;

import com.tiagoferreira.library.entity.Loan;
import com.tiagoferreira.library.model.loan.LoanRequest;

public interface ILoanService {

    Loan create(LoanRequest request);

    Loan retornedBook(Long id, LoanRequest request);
}
