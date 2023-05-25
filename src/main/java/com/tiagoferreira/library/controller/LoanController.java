package com.tiagoferreira.library.controller;

import com.tiagoferreira.library.model.loan.LoanMapper;
import com.tiagoferreira.library.model.loan.LoanRequest;
import com.tiagoferreira.library.model.loan.LoanResponse;
import com.tiagoferreira.library.service.loan.ILoanService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/loans")
public class LoanController {

    private final LoanMapper mapper;

    private final ILoanService service;


    public LoanController(LoanMapper mapper, ILoanService service) {
        this.mapper = mapper;
        this.service = service;
    }

    @PostMapping
    public LoanResponse create(@RequestBody LoanRequest request) {
        return mapper.toResponse(service.create(request));
    }

    @PatchMapping("/{id}")
    public LoanResponse retornedBook(@RequestBody LoanRequest request, @PathVariable Long id) {
        return mapper.toResponse(service.retornedBook(id, request));
    }


}
