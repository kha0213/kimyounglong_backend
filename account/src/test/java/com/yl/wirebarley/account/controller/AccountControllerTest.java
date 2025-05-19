package com.yl.wirebarley.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yl.wirebarley.account.domain.Account;
import com.yl.wirebarley.account.domain.Bank;
import com.yl.wirebarley.account.domain.dto.AccountCreateRequest;
import com.yl.wirebarley.account.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class AccountControllerTest {
    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccountRepository accountRepository;

    private AccountCreateRequest accountCreateRequest;
    private Account account;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
        accountRepository.deleteAll();

        accountCreateRequest = new AccountCreateRequest();
        accountCreateRequest.setAccountNumber("1234-5678-90");
        accountCreateRequest.setAccountHolder("A");
        accountCreateRequest.setBank(Bank.KB);

        account = Account.create(accountCreateRequest);

        accountRepository.save(account);
    }

    @Test
    @DisplayName("계좌 생성 - 성공")
    void createAccount_success() throws Exception {
        // Given
        AccountCreateRequest request = new AccountCreateRequest();
        request.setAccountNumber("12-12-12");
        request.setAccountHolder("B");
        request.setBank(Bank.KB);

        long before = accountRepository.count();
        // When & Then
        mockMvc.perform(post("/account")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());

        // Then
        assertThat(accountRepository.count()).isEqualTo(before + 1);
    }

    @Test
    @DisplayName("계좌 생성 - 실패 (필수 정보 누락)")
    void createAccount_failure_missingRequiredField() throws Exception {
        // Given
        AccountCreateRequest invalidRequest = new AccountCreateRequest();
        invalidRequest.setAccountHolder("C");
        invalidRequest.setBank(Bank.KB);
        // accountNumber 누락

        long before = accountRepository.count();

        // When & Then
        mockMvc.perform(post("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        // Then
        assertThat(accountRepository.count()).isEqualTo(before);
    }

    @Test
    @DisplayName("계좌 생성 - 실패 (계좌번호 중복)")
    void createAccount_failure_duplicateAccountNumber() throws Exception {
        // Given
        AccountCreateRequest duplicateRequest = new AccountCreateRequest();
        duplicateRequest.setAccountNumber("1234-5678-90"); // 이미 존재하는 계좌번호
        duplicateRequest.setAccountHolder("D");
        duplicateRequest.setBank(Bank.NH);

        long before = accountRepository.count();

        // When & Then
        mockMvc.perform(post("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateRequest)))
                .andExpect(status().isConflict());

        // Then
        assertThat(accountRepository.count()).isEqualTo(before);
    }

    @Test
    @DisplayName("계좌 삭제 - 성공")
    void deleteAccount_success() throws Exception {
        // Given
        Long accountId = account.getId();
        long before = accountRepository.count();

        // When & Then
        mockMvc.perform(delete("/account/{accountId}", accountId))
                .andExpect(status().isNoContent());

        // Then
        assertThat(accountRepository.count()).isEqualTo(before - 1);
        assertThat(accountRepository.findById(accountId)).isEmpty();
    }

    @Test
    @DisplayName("계좌 삭제 - 실패 (존재하지 않는 계좌)")
    void deleteAccount_failure_accountNotFound() throws Exception {
        // Given
        Long nonExistentAccountId = 999999L;

        // When & Then
        mockMvc.perform(delete("/account/{accountId}", nonExistentAccountId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("계좌 삭제 - 실패 (잘못된 ID 형식)")
    void deleteAccount_failure_invalidIdFormat() throws Exception {
        // When & Then
        mockMvc.perform(delete("/account/{accountId}", "invalid-id"))
                .andExpect(status().isBadRequest());
    }
}
