package com.tiagoferreira.library.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "NOME")
    private String nome;

    @Column(name = "AUTOR")
    private String autor;

    @Column(name = "ISBN")
    private String isbn;

    @OneToMany(mappedBy = "book")
    private List<Loan> loans;
}
