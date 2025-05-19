package com.yl.wirebarley.account.controller;

import com.yl.wirebarley.account.domain.dto.AccountCreateRequest;
import com.yl.wirebarley.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/account")
@RestController
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;
    
    @PostMapping
    public void save(@RequestBody AccountCreateRequest request) {
        // TODO: 계좌정보 저장
    }
    
    @DeleteMapping
    public void delete() {
        // TODO: 계좌정보 삭제
    }
}
