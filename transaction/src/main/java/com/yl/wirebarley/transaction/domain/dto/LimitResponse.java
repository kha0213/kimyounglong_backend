package com.yl.wirebarley.transaction.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Schema(description = "계좌의 남은 일별 한도 정보")
public class LimitResponse {
    
    @Schema(description = "계좌 ID", example = "1")
    private Long accountId;
    
    @Schema(description = "남은 일별 출금 한도", example = "1000000")
    private BigDecimal remainingWithdrawalLimit;
    
    @Schema(description = "남은 일별 이체 한도", example = "3000000")
    private BigDecimal remainingTransferLimit;
}