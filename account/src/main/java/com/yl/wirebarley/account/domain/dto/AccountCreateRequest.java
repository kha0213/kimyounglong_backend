package com.yl.wirebarley.account.domain.dto;

import com.yl.wirebarley.account.domain.Bank;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "계좌 생성 요청 정보")
public class AccountCreateRequest {
    
    @Schema(description = "은행 코드", example = "KB", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Bank bank;
    
    @Schema(description = "계좌번호", example = "123-456-789012", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty
    private String accountNumber;
    
    @Schema(description = "계좌 소유자명", example = "홍길동", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty
    private String accountHolder;
}
