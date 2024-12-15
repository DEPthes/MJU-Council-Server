package depth.mju.council.domain.minute.controller;

import depth.mju.council.domain.minute.dto.req.CreateMinuteReq;
import depth.mju.council.domain.minute.dto.req.ModifyMinuteReq;
import depth.mju.council.domain.minute.service.MinuteService;
import depth.mju.council.global.config.UserPrincipal;
import depth.mju.council.global.payload.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/minutes")
@RequiredArgsConstructor
public class MinuteController {
    private final MinuteService minuteService;
    @Operation(summary = "회의록 추가 API", description = "회의록 목록을 추가하는 API입니다.")
    @PostMapping
    public ResponseEntity<ApiResult> createMinute(
            @Parameter(description = "User의 토큰을 입력해주세요.", required = true) @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(description = "Multiaprt form-data 형식으로, 업로드할 파일 리스트입니다. 보낼 데이터가 없다면 빈 리스트로 전달해주세요.", required = true) @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @Parameter(description = "Schemas의 CreateMinuteReq를 참고해주세요.", required = true) @RequestPart(value = "createMinuteReq") CreateMinuteReq createMinuteReq) {
        minuteService.createMinute(userPrincipal.getId(), files, createMinuteReq);
        ApiResult result = ApiResult.builder()
                .check(true)
                .information("회의록을 추가했어요")
                .build();
        return ResponseEntity.ok(result);
    }
    @Operation(summary = "회의록 전체 조회 API", description = "회의록 목록을 조회하는 API입니다.")
    @GetMapping()
    public ResponseEntity<ApiResult> getAllMinute(
            @Parameter(description = "현재 페이지의 번호입니다. 0부터 시작합니다.", required = true) @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "한 페이지의 개수입니다.", required = true) @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "검색어입니다. 검색하지 않을 경우, 값을 보내지 않습니다.", required = false) @RequestParam Optional<String> keyword){
        ApiResult result = ApiResult.builder()
                .check(true)
                .information(minuteService.getAllMinute(page, size,keyword))
                .build();
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "회의록 상세 조회 API", description = "회의록 상세 내용을 조회하는 API입니다.")
    @GetMapping("/{minuteId}")
    public ResponseEntity<ApiResult> getMinute(
            @PathVariable Long minuteId) {
        ApiResult result = ApiResult.builder()
                .check(true)
                .information(minuteService.getMinute(minuteId))
                .build();
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "회의록 수정 API", description = "회의록을 수정하는 API입니다.")
    @PutMapping("/{minuteId}")
    public ResponseEntity<ApiResult> modifyMinute(
            @Parameter(description = "User의 토큰을 입력해주세요.", required = true) @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long minuteId,
            @RequestPart(value = "modifyMinuteReq") ModifyMinuteReq modifyMinuteReq,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        minuteService.modifyMinute(minuteId, modifyMinuteReq, files);
        ApiResult result = ApiResult.builder()
                .check(true)
                .information("회의록을 수정했어요")
                .build();
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "회의록 삭제 API", description = "회의록을 삭제하는 API입니다.")
    @DeleteMapping("/{minuteId}")
    public ResponseEntity<ApiResult> deleteMinute(
            @Parameter(description = "User의 토큰을 입력해주세요.", required = true) @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long minuteId) {
        minuteService.deleteMinute(minuteId);
        ApiResult result = ApiResult.builder()
                .check(true)
                .information("회의록을 삭제했어요")
                .build();
        return ResponseEntity.ok(result);
    }
    @Operation(summary = "회의록 전체 삭제 API", description = "회의록을 전체 삭제하는 API입니다.")
    @DeleteMapping
    public ResponseEntity<ApiResult> deleteAllMinute(@Parameter(description = "User의 토큰을 입력해주세요.", required = true) @AuthenticationPrincipal UserPrincipal userPrincipal) {
        minuteService.deleteAllMinute();
        ApiResult result = ApiResult.builder()
                .check(true)
                .information("회의록을 모두 삭제했어요")
                .build();
        return ResponseEntity.ok(result);
    }
}