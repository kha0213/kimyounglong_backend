package com.yl.wirebarley.account.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 은행 코드
 * <ul>
 * <li>KB - 국민은행</li>
 * <li>SHINHAN - 신한은행</li>
 * <li>NH - 농협은행</li>
 * <li>WOORI - 우리은행</li>
 * <li>KAKAO - 카카오뱅크</li>
 * </ul>
 */
@Getter
@AllArgsConstructor
@Schema(description = "은행 코드 (KB:국민은행, SHINHAN:신한은행, NH:농협은행, WOORI:우리은행, KAKAO:카카오뱅크)", 
        allowableValues = {"KB", "SHINHAN", "NH", "WOORI", "KAKAO"})
public enum Bank {
    /** 국민은행 */
    KB("국민은행"),
    /** 신한은행 */
    SHINHAN("신한은행"),
    /** 농협은행 */
    NH("농협은행"),
    /** 우리은행 */
    WOORI("우리은행"),
    /** 카카오뱅크 */
    KAKAO("카카오뱅크");

    private final String description;
}