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

    private String customer;

    private String customerEmail;

    @JoinColumn(name = "id_book")
    @ManyToOne
    private Book book;

    private LocalDate loanDate;

    private Boolean returned;
}
