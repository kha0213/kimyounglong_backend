package com.yl.wirebarley.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yl.wirebarley.account.domain.Account;
import com.yl.wirebarley.account.domain.Bank;
import com.yl.wirebarley.account.domain.dto.AccountCreateRequest;
import com.yl.wirebarley.account.repository.AccountRepository;
import com.yl.wirebarley.transaction.domain.dto.DepositRequest;
import com.yl.wirebarley.transaction.domain.dto.TransactionHistoryResponse;
import com.yl.wirebarley.transaction.domain.dto.TransferRequest;
import com.yl.wirebarley.transaction.domain.dto.WithdrawalRequest;
import com.yl.wirebarley.transaction.repository.TransactionsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class TransactionHistoryIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionsRepository transactionsRepository;

    private Account account1;
    private Account account2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
        
        transactionsRepository.deleteAll();
        accountRepository.deleteAll();

        // 테스트용 계좌 생성
        AccountCreateRequest request1 = new AccountCreateRequest();
        request1.setBank(Bank.KB);
        request1.setAccountNumber("123-456-789012");
        request1.setAccountHolder("홍길동");
        account1 = accountRepository.save(Account.create(request1));

        AccountCreateRequest request2 = new AccountCreateRequest();
        request2.setBank(Bank.SHINHAN);
        request2.setAccountNumber("987-654-321098");
        request2.setAccountHolder("김철수");
        account2 = accountRepository.save(Account.create(request2));
    }

    @Test
    @DisplayName("거래 내역 조회 - 입금, 출금, 이체 포함")
    void getTransactionHistory_WithAllTransactionTypes() throws Exception {
        // Given
        Long accountId1 = account1.getId();
        Long accountId2 = account2.getId();

        // 입금
        DepositRequest depositRequest = new DepositRequest();
        depositRequest.setAccountId(accountId1);
        depositRequest.setAmount(new BigDecimal("100000"));

        mockMvc.perform(post("/transactions/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositRequest)))
                .andExpect(status().isCreated());

        // 출금
        WithdrawalRequest withdrawalRequest = new WithdrawalRequest();
        withdrawalRequest.setAccountId(accountId1);
        withdrawalRequest.setAmount(new BigDecimal("20000"));

        mockMvc.perform(post("/transactions/withdrawal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(withdrawalRequest)))
                .andExpect(status().isCreated());

        // 이체 (account1 -> account2)
        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setAccountId(accountId1);
        transferRequest.setTargetAccountId(accountId2);
        transferRequest.setAmount(new BigDecimal("30000"));
        transferRequest.setSenderMemo("친구에게 송금");
        transferRequest.setReceiverMemo("용돈");

        mockMvc.perform(post("/transactions/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferRequest)))
                .andExpect(status().isCreated());

        // When
        MvcResult result = mockMvc.perform(get("/transactions/history/{accountId}", accountId1))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        String responseBody = result.getResponse().getContentAsString();
        List<TransactionHistoryResponse> history = objectMapper.readValue(responseBody, new TypeReference<>() {});

        // 거래내역 검증
        assertThat(history).hasSize(3);
        
        // 최신순 정렬 확인 (이체 -> 출금 -> 입금)
        assertThat(history.get(0).getType().name()).isEqualTo("TRANSFER");
        assertThat(history.get(0).getAmount()).isEqualByComparingTo("30000");
        assertThat(history.get(0).getFee()).isEqualByComparingTo("300"); // 1% 수수료
        assertThat(history.get(0).getBalanceChange()).isEqualByComparingTo("-30300"); // 금액 + 수수료
        assertThat(history.get(0).getCounterpartyAccountId()).isEqualTo(accountId2);

        assertThat(history.get(1).getType().name()).isEqualTo("WITHDRAWAL");
        assertThat(history.get(1).getAmount()).isEqualByComparingTo("20000");
        assertThat(history.get(1).getBalanceChange()).isEqualByComparingTo("-20000");

        assertThat(history.get(2).getType().name()).isEqualTo("DEPOSIT");
        assertThat(history.get(2).getAmount()).isEqualByComparingTo("100000");
        assertThat(history.get(2).getBalanceChange()).isEqualByComparingTo("100000");
    }

    @Test
    @DisplayName("거래 내역 조회 - 이체 수신 내역")
    void getTransactionHistory_TransferReceive() throws Exception {
        // Given
        Long accountId1 = account1.getId();
        Long accountId2 = account2.getId();

        // 계좌1에 입금
        DepositRequest depositRequest = new DepositRequest();
        depositRequest.setAccountId(accountId1);
        depositRequest.setAmount(new BigDecimal("100000"));

        mockMvc.perform(post("/transactions/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositRequest)))
                .andExpect(status().isCreated());

        // 이체 (account1 -> account2)
        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setAccountId(accountId1);
        transferRequest.setTargetAccountId(accountId2);
        transferRequest.setAmount(new BigDecimal("50000"));
        transferRequest.setSenderMemo("친구에게 송금");
        transferRequest.setReceiverMemo("용돈 감사합니다");

        mockMvc.perform(post("/transactions/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferRequest)))
                .andExpect(status().isCreated());

        // When
        MvcResult result = mockMvc.perform(get("/transactions/history/{accountId}", accountId2))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        String responseBody = result.getResponse().getContentAsString();
        List<TransactionHistoryResponse> history = objectMapper.readValue(responseBody,
                new TypeReference<List<TransactionHistoryResponse>>() {});

        // 거래내역 검증
        assertThat(history).hasSize(1);
        assertThat(history.get(0).getType().name()).isEqualTo("TRANSFER_RECEIVE");
        assertThat(history.get(0).getAmount()).isEqualByComparingTo("50000");
        assertThat(history.get(0).getFee()).isEqualByComparingTo("0"); // 수신자는 수수료 없음
        assertThat(history.get(0).getBalanceChange()).isEqualByComparingTo("50000");
        assertThat(history.get(0).getDescription()).isEqualTo("용돈 감사합니다");
        assertThat(history.get(0).getCounterpartyAccountId()).isEqualTo(accountId1);
    }

    @Test
    @DisplayName("거래 내역 조회 - 존재하지 않는 계좌")
    void getTransactionHistory_AccountNotFound() throws Exception {
        // Given
        Long nonExistentAccountId = 999999L;

        // When & Then
        mockMvc.perform(get("/transactions/history/{accountId}", nonExistentAccountId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("거래 내역 조회 - 거래 내역이 없는 계좌")
    void getTransactionHistory_EmptyHistory() throws Exception {
        // Given
        AccountCreateRequest newAccountRequest = new AccountCreateRequest();
        newAccountRequest.setBank(Bank.KAKAO);
        newAccountRequest.setAccountNumber("555-666-777888");
        newAccountRequest.setAccountHolder("박영희");
        Account newAccount = accountRepository.save(Account.create(newAccountRequest));

        // When
        MvcResult result = mockMvc.perform(get("/transactions/history/{accountId}", newAccount.getId()))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        String responseBody = result.getResponse().getContentAsString();
        List<TransactionHistoryResponse> history = objectMapper.readValue(responseBody,
                new TypeReference<List<TransactionHistoryResponse>>() {});

        assertThat(history).isEmpty();
    }

    @Test
    @DisplayName("거래 내역 조회 - 잘못된 계좌 ID 형식")
    void getTransactionHistory_InvalidAccountIdFormat() throws Exception {
        // When & Then
        mockMvc.perform(get("/transactions/history/{accountId}", "invalid-id"))
                .andExpect(status().isBadRequest());
    }
}
