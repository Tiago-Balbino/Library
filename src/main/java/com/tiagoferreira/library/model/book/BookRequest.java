package com.tiagoferreira.library.model.book;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BookRequest {

    private String nome;

    private String autor;

    private String isbn;
}
