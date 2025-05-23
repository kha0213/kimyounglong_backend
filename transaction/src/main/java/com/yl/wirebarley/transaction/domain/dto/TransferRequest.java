package com.yl.wirebarley.transaction.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "이체 요청 정보")
public class TransferRequest {
    
    @Schema(description = "송금 계좌 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Long accountId;
    
    @Schema(description = "수금 계좌 ID", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Long targetAccountId;
    
    @Schema(description = "이체 금액", example = "15000", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    @DecimalMin(value = "0.01", message = "이체 금액은 0.01 이상이어야 합니다.")
    private BigDecimal amount;
    
    @Schema(description = "수금자 메모", example = "생일 축하 선물")
    private String receiverMemo;
    
    @Schema(description = "송금자 메모", example = "친구에게 이체")
    private String senderMemo;
}
