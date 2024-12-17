package depth.mju.council.domain.promise.controller;

import depth.mju.council.domain.promise.dto.req.CreatePromiseReq;
import depth.mju.council.domain.promise.dto.req.ModifyPromiseReq;
import depth.mju.council.domain.promise.service.PromiseService;
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
@RequiredArgsConstructor
@RequestMapping("/api/v1/promise")
public class PromiseController {
    private final PromiseService promiseService;
    @Operation(summary = "공약 추가 API", description = "공약 목록을 추가하는 API입니다.")
    @PostMapping("/{promiseTitle}")
    public ResponseEntity<ApiResult> createPromise(
            @Parameter(description = "User의 토큰을 입력해주세요.", required = true) @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable String promiseTitle, @RequestBody CreatePromiseReq createPromiseCategoryReq){
        promiseService.createPromise(userPrincipal.getId(), promiseTitle,createPromiseCategoryReq);
        ApiResult result = ApiResult.builder()
                .check(true)
                .information("공약을 추가했어요")
                .build();
        return ResponseEntity.ok(result);
    }
    @Operation(summary = "공약 조회 API", description = "공약 목록을 조회하는 API입니다.")
    @GetMapping("/{promiseTitle}")
    public ResponseEntity<ApiResult> getPromise(@PathVariable String promiseTitle){
        ApiResult result = ApiResult.builder()
                .check(true)
                .information(promiseService.getPromise(promiseTitle))
                .build();
        return ResponseEntity.ok(result);
    }
    @Operation(summary = "공약 수정 API", description = "공약 목록을 수정하는 API입니다.")
    @PatchMapping("/{promiseId}")
    public ResponseEntity<ApiResult> modifyPromise(
            @Parameter(description = "User의 토큰을 입력해주세요.", required = true) @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long promiseId,@RequestBody ModifyPromiseReq modifyPromiseReq){
        promiseService.modifyPromise(promiseId,modifyPromiseReq);
        ApiResult result = ApiResult.builder()
                .check(true)
                .information("공약을 수정했어요")
                .build();
        return ResponseEntity.ok(result);
    }
    @Operation(summary = "공약 삭제 API", description = "공약 목록을 삭제하는 API입니다.")
    @DeleteMapping("/{promiseId}")
    public ResponseEntity<ApiResult> deletePromise(
            @Parameter(description = "User의 토큰을 입력해주세요.", required = true) @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long promiseId){
        promiseService.deletePromise(promiseId);
        ApiResult result = ApiResult.builder()
                .check(true)
                .information("공약을 삭제했어요")
                .build();
        return ResponseEntity.ok(result);
    }
}
