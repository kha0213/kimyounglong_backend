package com.yl.wirebarley.transaction.controller;

import com.yl.wirebarley.transaction.domain.dto.DepositRequest;
import com.yl.wirebarley.transaction.domain.dto.LimitResponse;
import com.yl.wirebarley.transaction.domain.dto.TransactionResponse;
import com.yl.wirebarley.transaction.domain.dto.TransferRequest;
import com.yl.wirebarley.transaction.domain.dto.WithdrawalRequest;
import com.yl.wirebarley.transaction.service.TransactionsService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@Slf4j
@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {
    
    private final TransactionsService transactionsService;
    
    /**
     * 계좌 입금 API
     */
    @PostMapping("/deposit")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse deposit(@RequestBody @Valid DepositRequest request) {
        log.info("Deposit request received: {}", request);
        return transactionsService.deposit(request);
    }
    
    /**
     * 계좌 출금 API
     */
    @PostMapping("/withdrawal")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse withdrawal(@RequestBody @Valid WithdrawalRequest request) {
        log.info("Withdrawal request received: {}", request);
        return transactionsService.withdrawal(request);
    }
    
    /**
     * 계좌 이체 API
     */
    @PostMapping("/transfer")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse transfer(@RequestBody @Valid TransferRequest request) {
        log.info("Transfer request received: {}", request);
        return transactionsService.transfer(request);
    }
    
    /**
     * 계좌의 남은 일별 한도 조회 API
     */
    @GetMapping("/limits/{accountId}")
    public LimitResponse getLimits(@PathVariable @NotNull Long accountId) {
        log.info("Getting daily limits for account: {}", accountId);
        
        BigDecimal withdrawalLimit = transactionsService.getRemainingWithdrawalLimit(accountId);
        BigDecimal transferLimit = transactionsService.getRemainingTransferLimit(accountId);
        
        return new LimitResponse(accountId, withdrawalLimit, transferLimit);
    }
}