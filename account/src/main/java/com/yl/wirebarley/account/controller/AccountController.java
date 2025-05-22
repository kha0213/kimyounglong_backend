package com.yl.wirebarley.account.controller;

import com.yl.wirebarley.account.domain.dto.AccountCreateRequest;
import com.yl.wirebarley.account.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/account")
@RestController
@RequiredArgsConstructor
@Tag(name = "Account", description = "계좌 관리 API")
public class AccountController {
    private final AccountService accountService;

    @Operation(
            summary = "계좌 생성",
            description = "새로운 계좌를 생성합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "계좌가 성공적으로 생성되었습니다.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 데이터입니다.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "이미 존재하는 계좌번호입니다.",
                    content = @Content
            )
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public void save(@RequestBody @Valid AccountCreateRequest request) {
        log.info("Saving account : {}", request);
        accountService.save(request);
    }

    @Operation(
            summary = "계좌 삭제",
            description = "지정된 ID의 계좌를 삭제합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "계좌가 성공적으로 삭제되었습니다.",
                    content = @Content
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
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{accountId}")
    public void delete(
            @Parameter(description = "삭제할 계좌의 ID", required = true, example = "1")
            @Valid @NotNull @Min(value = 0) @PathVariable Long accountId
    ) {
        log.info("Deleting account : {}", accountId);
        accountService.delete(accountId);
    }
}
