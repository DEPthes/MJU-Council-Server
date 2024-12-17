package depth.mju.council.domain.alliance.controller;

import depth.mju.council.domain.alliance.dto.req.CreateAllianceReq;
import depth.mju.council.domain.alliance.dto.req.ModifyAllianceReq;
import depth.mju.council.domain.alliance.service.AllianceService;
import depth.mju.council.global.config.UserPrincipal;
import depth.mju.council.global.payload.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/alliances")
@RequiredArgsConstructor
public class AllianceController {
    private final AllianceService allianceService;

    @Operation(summary = "제휴 상세 조회")
    @GetMapping("/{allianceId}")
    public ResponseEntity<ApiResult> getAlliance(
            @Parameter(description = "조회하고자 하는 제휴의 id를 입력해주세요.", required = true) @PathVariable Long allianceId
    ) {
        ApiResult apiResult = ApiResult.builder()
                .check(true)
                .information(allianceService.getAlliance(allianceId))
                .message("제휴 " + allianceId +"번을 조회합니다.")
                .build();
        return ResponseEntity.ok(apiResult);
    }

    @Operation(summary = "제휴 목록 조회")
    @GetMapping("")
    public ResponseEntity<ApiResult> getAllAlliance(
            @Parameter(description = "현재 페이지의 번호입니다. 0부터 시작합니다.", required = true) @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "한 페이지의 개수입니다.", required = true) @RequestParam(defaultValue = "9") int size
    ) {
        ApiResult apiResult = ApiResult.builder()
                .check(true)
                .information(allianceService.getAllAlliance(page, size))
                .message("제휴 목록을 조회합니다.")
                .build();
        return ResponseEntity.ok(apiResult);
    }

    @Operation(summary = "제휴 등록")
    @PostMapping()
    public ResponseEntity<ApiResult> createAlliance(
            @Parameter(description = "User의 토큰을 입력해주세요.", required = true) @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(description = "Multiaprt form-data 형식으로, 업로드할 이미지의 리스트입니다. 보낼 데이터가 없다면 빈 리스트로 전달해주세요.", required = true) @RequestPart List<MultipartFile> images,
            @Parameter(description = "Multiaprt form-data 형식으로, 업로드할 파일의 리스트입니다. 보낼 데이터가 없다면 빈 리스트로 전달해주세요.", required = true) @RequestPart List<MultipartFile> files,
            @Parameter(description = "Schemas의 CreateAllianceReq를 참고해주세요.", required = true) @Valid @RequestPart CreateAllianceReq createAllianceReq
            ) {
        allianceService.createAlliance(userPrincipal, images, files, createAllianceReq);
        ApiResult apiResult = ApiResult.builder()
                .check(true)
                .message("제휴가 등록되었습니다.")
                .build();
        return ResponseEntity.ok(apiResult);
    }

    @Operation(summary = "제휴 전체 삭제")
    @DeleteMapping()
    public ResponseEntity<ApiResult> deleteAllAlliance(
            @Parameter(description = "User의 토큰을 입력해주세요.", required = true) @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        allianceService.deleteAllAlliance();
        ApiResult apiResult = ApiResult.builder()
                .check(true)
                .message("제휴가 전체 삭제되었습니다.")
                .build();
        return ResponseEntity.ok(apiResult);
    }

    @Operation(summary = "제휴 삭제")
    @DeleteMapping("/{allianceId}")
    public ResponseEntity<ApiResult> deleteAlliance(
            @Parameter(description = "User의 토큰을 입력해주세요.", required = true) @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(description = "삭제하고자 하는 제휴의 id를 입력해주세요.", required = true) @PathVariable Long allianceId
    ) {
        allianceService.deleteAlliance(allianceId);
        ApiResult apiResult = ApiResult.builder()
                .check(true)
                .message("제휴가 삭제되었습니다.")
                .build();
        return ResponseEntity.ok(apiResult);
    }

    @Operation(summary = "제휴 수정")
    @PutMapping("/{allianceId}")
    public ResponseEntity<ApiResult> modifyAlliance(
            @Parameter(description = "User의 토큰을 입력해주세요.", required = true) @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(description = "수정하고자 하는 제휴의 id를 입력해주세요.", required = true) @PathVariable Long allianceId,
            @Parameter(description = "Multiaprt form-data 형식으로, 업로드할 이미지의 리스트입니다. 보낼 데이터가 없다면 빈 리스트로 전달해주세요.", required = true) @RequestPart List<MultipartFile> images,
            @Parameter(description = "Multiaprt form-data 형식으로, 업로드할 파일의 리스트입니다. 보낼 데이터가 없다면 빈 리스트로 전달해주세요.", required = true) @RequestPart List<MultipartFile> files,
            @Parameter(description = "Schemas의 ModifyAllianceReq를 참고해주세요.", required = true) @Valid @RequestPart ModifyAllianceReq modifyAllianceReq
            ) {
        allianceService.modifyAlliance(allianceId, images, files, modifyAllianceReq);
        ApiResult apiResult = ApiResult.builder()
                .check(true)
                .message("제휴가 수정되었습니다.")
                .build();
        return ResponseEntity.ok(apiResult);
    }
}
