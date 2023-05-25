package com.tiagoferreira.library.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "CUSTOMER")
    private String customer;

    @Column(name = "CUSTOMER_EMAIL")
    private String customerEmail;

    @JoinColumn(name = "id_book")
    @ManyToOne
    private Book book;

    @Column(name = "LOAN_DATE")
    private LocalDate loanDate;

    @Column(name = "RETURNED")
    private Boolean returned;
}
