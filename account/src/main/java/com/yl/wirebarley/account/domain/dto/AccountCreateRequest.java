package com.yl.wirebarley.account.domain.dto;

import com.yl.wirebarley.account.domain.Bank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AccountCreateRequest {
    @NotNull
    private Bank bank;
    @NotEmpty
    private String accountNumber;
    @NotEmpty
    private String accountHolder;
}
