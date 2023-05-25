package com.tiagoferreira.library.repository;

import com.tiagoferreira.library.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    @Query("select case when (count(l.id) > 0) then true else false end from Loan l where l.book.id = :idBook")
    boolean existsByBookAndNotReturned(@Param("idBook") Long idBook);
}
