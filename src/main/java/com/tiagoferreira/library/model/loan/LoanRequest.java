package com.tiagoferreira.library.model.loan;

import com.tiagoferreira.library.model.book.BookRequest;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoanRequest {

    private String customer;

    private String customerEmail;

    private Long idBook;

    private LocalDate loanDate;

    private Boolean returned;
}
