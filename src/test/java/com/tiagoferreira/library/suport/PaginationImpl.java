package com.tiagoferreira.library.suport;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PaginationImpl<T> {

    private List<T> content;
    private int number;
    private int size;
    private Long totalElements;
    private JsonNode pageable;
    private boolean last;
    private int totalPages;
    private JsonNode sort;
    private boolean first;
    private int numberOfElements;

}
