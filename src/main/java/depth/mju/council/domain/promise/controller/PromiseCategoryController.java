package depth.mju.council.domain.promise.controller;

import depth.mju.council.domain.promise.service.PromiseCategoryService;
import depth.mju.council.global.config.UserPrincipal;
import depth.mju.council.global.payload.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/v1/promise-category")
@RequiredArgsConstructor
public class PromiseCategoryController {
    private final PromiseCategoryService promiseCategoryService;
    @Operation(summary = "정책 추가 API", description = "정책 목록을 추가하는 API입니다.")
    @PostMapping("/{promiseTitle}")
    public ResponseEntity<ApiResult> createPromiseCategory(
            @Parameter(description = "User의 토큰을 입력해주세요.", required = true) @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable String promiseTitle) {
        promiseCategoryService.createPromiseCategory(userPrincipal.getId(), promiseTitle);
        ApiResult result = ApiResult.builder()
                .check(true)
                .information("정책 목록을 추가했어요")
                .build();
        return ResponseEntity.ok(result);
    }
    @Operation(summary = "정책 조회 API", description = "정책 목록을 조회하는 API입니다.")
    @GetMapping()
    public ResponseEntity<ApiResult> getPromiseCategory() {
        ApiResult result = ApiResult.builder()
                .check(true)
                .information(promiseCategoryService.getPromiseCategory())
                .build();
        return ResponseEntity.ok(result);
    }
    @Operation(summary = "정책 수정 API", description = "정책 목록을 수정하는 API입니다.")
    @PatchMapping("/{promiseCategoryId}/{promiseTitle}")
    public ResponseEntity<ApiResult> modifyPromiseCategory(
            @Parameter(description = "User의 토큰을 입력해주세요.", required = true) @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long promiseCategoryId,
            @PathVariable String promiseTitle) {
        promiseCategoryService.modifyPromiseCategory(promiseCategoryId, promiseTitle);
        ApiResult result = ApiResult.builder()
                .check(true)
                .information("정책 목록을 수정했어요")
                .build();
        return ResponseEntity.ok(result);
    }
    @Operation(summary = "정책 삭제 API", description = "정책 목록을 삭제하는 API입니다.")
    @DeleteMapping("/{promiseCategoryId}")
    public ResponseEntity<ApiResult> deletePromiseCategory(
            @Parameter(description = "User의 토큰을 입력해주세요.", required = true) @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long promiseCategoryId) {
        promiseCategoryService.deletePromiseCategory(promiseCategoryId);
        ApiResult result = ApiResult.builder()
                .check(true)
                .information("정책 목록을 삭제했어요")
                .build();
        return ResponseEntity.ok(result);
    }

}
