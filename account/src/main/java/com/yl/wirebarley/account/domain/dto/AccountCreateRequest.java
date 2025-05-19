package com.yl.wirebarley.account.domain.dto;

import com.yl.wirebarley.account.domain.Bank;
import lombok.Data;

@Data
public class AccountCreateRequest {
    private Bank bank;
    private String accountNumber;
    private String accountHolder;
}
