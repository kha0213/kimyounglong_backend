package com.yl.wirebarley.transaction.repository;

import com.yl.wirebarley.transaction.domain.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionsRepository extends JpaRepository<Transactions, Long> {
    List<Transactions> findAllByAccountIdOrderByTransactionTimeDesc(@Param("accountId") Long accountId);
}
