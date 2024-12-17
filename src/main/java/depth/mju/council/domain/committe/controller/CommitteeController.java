package depth.mju.council.domain.committe.controller;

import depth.mju.council.domain.committe.service.CommitteeService;
import depth.mju.council.domain.committe.dto.req.CreateCommitteeReq;
import depth.mju.council.global.config.UserPrincipal;
import depth.mju.council.global.payload.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/committees")
@RequiredArgsConstructor
public class CommitteeController {
    private final CommitteeService committeeService;

    @Operation(summary = "중운위(단과대) 조회")
    @GetMapping("")
    public ResponseEntity<ApiResult> getAllCommittees() {
        ApiResult apiResult = ApiResult.builder()
                .check(true)
                .information(committeeService.getAllCommittees())
                .message("중운위(단과대)를 성공적으로 조회했습니다.")
                .build();
        return ResponseEntity.ok(apiResult);
    }

    @Operation(summary = "중운위(단과대) 등록")
    @PostMapping("")
    public ResponseEntity<?> createCommittee(
            @RequestPart("request") CreateCommitteeReq request,
            @RequestPart("image") MultipartFile image,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        committeeService.createCommittee(request, image, userPrincipal);
        return ResponseEntity.ok(
                ApiResult.builder()
                        .check(true)
                        .message("중운위(단과대)가 등록되었습니다.")
                        .build()
        );
    }

    @Operation(summary = "중운위(단과대) 수정")
    @PutMapping("/{committeeId}")
    public ResponseEntity<?> updateCommittee(
            @PathVariable Long committeeId,
            @RequestPart("request") CreateCommitteeReq request,
            @RequestPart("image") MultipartFile image,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        committeeService.updateCommittee(committeeId, request, image, userPrincipal);
        return ResponseEntity.ok(
                ApiResult.builder()
                        .check(true)
                        .message("중운위(단과대)가 수정되었습니다.")
                        .build()
        );
    }

    @Operation(summary = "중운위(단과대) 삭제")
    @DeleteMapping("/{committeeId}")
    public ResponseEntity<?> deleteCommittee(@PathVariable Long committeeId,
                                             @AuthenticationPrincipal UserPrincipal userPrincipal) {
        committeeService.deleteCommittee(committeeId, userPrincipal);
        return ResponseEntity.ok(
                ApiResult.builder()
                        .check(true)
                        .message("중운위(단과대)가 삭제되었습니다.")
                        .build()
        );
    }
}
