package com.yl.wirebarley.transaction.controller;

import com.yl.wirebarley.transaction.domain.dto.DepositRequest;
import com.yl.wirebarley.transaction.domain.dto.LimitResponse;
import com.yl.wirebarley.transaction.domain.dto.TransactionResponse;
import com.yl.wirebarley.transaction.domain.dto.TransferRequest;
import com.yl.wirebarley.transaction.domain.dto.WithdrawalRequest;
import com.yl.wirebarley.transaction.service.TransactionsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Transaction", description = "거래 관리 API")
public class TransactionController {
    
    private final TransactionsService transactionsService;
    
    @Operation(
            summary = "계좌 입금",
            description = "지정된 계좌에 입금을 진행합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "입금이 성공적으로 처리되었습니다.",
                    content = @Content(schema = @Schema(implementation = TransactionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 데이터입니다.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "계좌를 찾을 수 없습니다.",
                    content = @Content
            )
    })
    @PostMapping("/deposit")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse deposit(@RequestBody @Valid DepositRequest request) {
        log.info("Deposit request received: {}", request);
        return transactionsService.deposit(request);
    }
    
    @Operation(
            summary = "계좌 출금",
            description = "지정된 계좌에서 출금을 진행합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "출금이 성공적으로 처리되었습니다.",
                    content = @Content(schema = @Schema(implementation = TransactionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 데이터 또는 잔액 부족입니다.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "계좌를 찾을 수 없습니다.",
                    content = @Content
            )
    })
    @PostMapping("/withdrawal")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse withdrawal(@RequestBody @Valid WithdrawalRequest request) {
        log.info("Withdrawal request received: {}", request);
        return transactionsService.withdrawal(request);
    }
    
    @Operation(
            summary = "계좌 이체",
            description = "한 계좌에서 다른 계좌로 이체를 진행합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "이체가 성공적으로 처리되었습니다.",
                    content = @Content(schema = @Schema(implementation = TransactionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 데이터 또는 잔액/한도 부족입니다.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "계좌를 찾을 수 없습니다.",
                    content = @Content
            )
    })
    @PostMapping("/transfer")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse transfer(@RequestBody @Valid TransferRequest request) {
        log.info("Transfer request received: {}", request);
        return transactionsService.transfer(request);
    }
    
    @Operation(
            summary = "일별 거래 한도 조회",
            description = "지정된 계좌의 남은 일별 출금 및 이체 한도를 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "한도 정보를 성공적으로 조회했습니다.",
                    content = @Content(schema = @Schema(implementation = LimitResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 계좌 ID입니다.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "계좌를 찾을 수 없습니다.",
                    content = @Content
            )
    })
    @GetMapping("/limits/{accountId}")
    public LimitResponse getLimits(
            @Parameter(description = "한도를 조회할 계좌의 ID", required = true, example = "1")
            @PathVariable @NotNull Long accountId
    ) {
        log.info("Getting daily limits for account: {}", accountId);
        
        BigDecimal withdrawalLimit = transactionsService.getRemainingWithdrawalLimit(accountId);
        BigDecimal transferLimit = transactionsService.getRemainingTransferLimit(accountId);
        
        return new LimitResponse(accountId, withdrawalLimit, transferLimit);
    }
}