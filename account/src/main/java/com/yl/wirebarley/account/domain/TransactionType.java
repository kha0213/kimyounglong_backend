package com.yl.wirebarley.account.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TransactionType {
    DEPOSIT("입금"),
    WITHDRAWAL("출금"),
    TRANSFER_IN("이체_입금"),
    TRANSFER_OUT("이체_출금");

    private final String description;
}
