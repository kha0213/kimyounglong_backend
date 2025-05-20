package com.yl.wirebarley.transaction.repository;

import com.yl.wirebarley.transaction.domain.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionsRepository extends JpaRepository<Transactions, Long> {
}
