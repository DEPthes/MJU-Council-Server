package depth.mju.council.domain.regulation.controller;

import depth.mju.council.domain.regulation.dto.req.CreateRegulationReq;
import depth.mju.council.domain.regulation.dto.req.ModifyRegulationReq;
import depth.mju.council.domain.regulation.service.RegulationService;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/regulations")
@RequiredArgsConstructor
public class RegulationController {
    private final RegulationService regulationService;
    @Operation(summary = "학생회칙 추가 API", description = "학생회칙을 추가하는 API입니다.")
    @PostMapping
    public ResponseEntity<ApiResult> createRegulation(
            @Parameter(description = "User의 토큰을 입력해주세요.", required = true) @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(description = "Schemas의 CreateRegulationReq를 참고해주세요.", required = true) @RequestPart CreateRegulationReq createRegulationReq,
            @Parameter(description = "Multiaprt form-data 형식으로, 업로드할 파일 리스트입니다. 보낼 데이터가 없다면 빈 리스트로 전달해주세요.", required = true)
            @RequestPart(value = "file", required = false) List<MultipartFile> file) {
        regulationService.createRegulation(userPrincipal.getId(), file,createRegulationReq);
        ApiResult result = ApiResult.builder()
                .check(true)
                .information("학생회칙을 추가했어요")
                .build();
        return ResponseEntity.ok(result);
    }
    @Operation(summary = "학생회칙 전체 조회 API", description = "학생회칙 목록을 전체 조회하는 API입니다.")
    @GetMapping
    public ResponseEntity<ApiResult> getAllRegulation(
            @Parameter(description = "현재 페이지의 번호입니다. 0부터 시작합니다.", required = true) @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "한 페이지의 개수입니다.", required = true) @RequestParam(defaultValue = "10") int size){
//            @Parameter(description = "검색어입니다. 검색하지 않을 경우, 값을 보내지 않습니다.", required = false) @RequestParam Optional<String> keyword) {
        ApiResult result = ApiResult.builder()
                .check(true)
                .information(regulationService.getAllRegulation(page, size))
                .build();
        return ResponseEntity.ok(result);
    }
    @Operation(summary = "학생회칙 상세 조회 API", description = "학생회칙 상세 내용을 조회하는 API입니다.")
    @GetMapping("/{regulationId}")
    public ResponseEntity<ApiResult> getRegulation(
            @PathVariable Long regulationId) {
        ApiResult result = ApiResult.builder()
                .check(true)
                .information(regulationService.getRegulation(regulationId))
                .build();
        return ResponseEntity.ok(result);
    }
    @Operation(summary = "학생회칙 수정 API", description = "학생회칙을 수정하는 API입니다.")
    @PatchMapping("/{regulationId}")
    public ResponseEntity<ApiResult> modifyRegulation(
            @Parameter(description = "User의 토큰을 입력해주세요.", required = true) @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long regulationId,
            @RequestPart ModifyRegulationReq modifyRegulationReq,
            @RequestPart(value = "file", required = false) List<MultipartFile> file) {
        regulationService.modifyRegulation(regulationId, file, modifyRegulationReq);
        ApiResult result = ApiResult.builder()
                .check(true)
                .information("학생회칙을 수정했어요")
                .build();
        return ResponseEntity.ok(result);
    }
    @Operation(summary = "학생회칙 삭제 API", description = "학생회칙을 삭제하는 API입니다.")
    @DeleteMapping("/{regulationId}")
    public ResponseEntity<ApiResult> deleteRegulation(
            @Parameter(description = "User의 토큰을 입력해주세요.", required = true) @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long regulationId) {
        regulationService.deleteRegulation(regulationId);
        ApiResult result = ApiResult.builder()
                .check(true)
                .information("학생회칙을 삭제했어요")
                .build();
        return ResponseEntity.ok(result);
    }
    @Operation(summary = "학생회칙 전체 삭제 API", description = "학생회칙을 전체 삭제하는 API입니다.")
    @DeleteMapping
    public ResponseEntity<ApiResult> deleteAllRegulation(@Parameter(description = "User의 토큰을 입력해주세요.", required = true) @AuthenticationPrincipal UserPrincipal userPrincipal) {
        regulationService.deleteAllRegulation();
        ApiResult result = ApiResult.builder()
                .check(true)
                .information("학생회칙을 모두 삭제했어요")
                .build();
        return ResponseEntity.ok(result);
    }
}
