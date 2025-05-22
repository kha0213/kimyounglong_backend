package com.yl.wirebarley.transaction;

import com.yl.wirebarley.account.api.AccountOperations;
import com.yl.wirebarley.account.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(classes = TransactionApplication.class)
class TransactionsApplicationTest {
    
    @MockitoBean
    private AccountOperations accountOperations;
    
    @MockitoBean
    private AccountRepository accountRepository;
    
    @Test
    void contextLoads() {
    }
}