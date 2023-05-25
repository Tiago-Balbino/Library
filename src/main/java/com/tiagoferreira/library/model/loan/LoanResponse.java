package com.tiagoferreira.library.model.loan;

import com.tiagoferreira.library.model.book.BookResponse;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoanResponse {

    private Long id;

    private String customer;

    private String customerEmail;

    private BookResponse book;

    private LocalDate loanDate;

    private Boolean returned;
}
