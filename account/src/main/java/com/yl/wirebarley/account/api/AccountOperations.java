package com.yl.wirebarley.account.api;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * 계좌 관련 작업을 수행하는 외부 모듈용 API 인터페이스
 */
public interface AccountOperations {
    
    /**
     * 계좌 정보 조회
     *
     * @param accountId 계좌 ID
     * @return 계좌 정보 DTO
     */
    Optional<AccountDto> getAccount(Long accountId);
    
    /**
     * 계좌 잔액 변경
     * 
     * @param accountId 계좌 ID
     * @param amount 변경할 금액 (양수: 입금, 음수: 출금)
     * @return 변경된 계좌 정보 DTO
     * @throws com.yl.wirebarley.account.exception.AccountException 계좌를 찾을 수 없는 경우 또는 잔액 부족 시
     */
    AccountDto updateBalance(Long accountId, BigDecimal amount);
}
