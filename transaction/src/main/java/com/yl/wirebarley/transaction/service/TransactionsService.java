package com.yl.wirebarley.transaction.service;

import com.yl.wirebarley.transaction.repository.TransactionsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class TransactionsService {
    private final TransactionsRepository transactionsRepository;

    public void deposit() {

    }

    public void withdrawal() {

    }

    public void transfer() {

    }
}
