package com.tiagoferreira.library.model.loan;

import com.tiagoferreira.library.entity.Loan;
import com.tiagoferreira.library.model.book.BookMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {BookMapper.class})
public interface LoanMapper {

    LoanResponse toResponse(Loan loan);
}
