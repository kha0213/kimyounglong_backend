package com.yl.wirebarley.account.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Bank {
    KB("국민은행"),
    SHINHAN("신한은행"),
    NH("농협은행"),
    WOORI("우리은행"),
    KAKAO("카카오뱅크");

    private final String description;
}