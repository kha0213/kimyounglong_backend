package com.yl.wirebarley.transaction.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "출금 요청 정보")
public class WithdrawalRequest {
    
    @Schema(description = "출금할 계좌 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Long accountId;
    
    @Schema(description = "출금 금액", example = "5000", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    @DecimalMin(value = "0.01", message = "출금 금액은 0.01 이상이어야 합니다.")
    private BigDecimal amount;
}
