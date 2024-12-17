package depth.mju.council.domain.user.controller;

import depth.mju.council.domain.user.dto.req.UpdateCouncilReq;
import depth.mju.council.domain.user.service.CouncilService;
import depth.mju.council.global.config.UserPrincipal;
import depth.mju.council.global.payload.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/council")
@RequiredArgsConstructor
public class CouncilController {

    private final CouncilService councilService;

    @Operation(summary = "총학정보 조회")
    @GetMapping("")
    public ResponseEntity<ApiResult> getCouncil() {
        ApiResult apiResult = ApiResult.builder()
                .check(true)
                .information(councilService.getCouncil())
                .message("총학정보를 성공적으로 조회했습니다.")
                .build();
        return ResponseEntity.ok(apiResult);
    }

    @Operation(summary = "총학정보 수정")
    @PutMapping("")
    public ResponseEntity<?> updateCouncil(
            @RequestPart("request") UpdateCouncilReq request,
            @RequestPart("image") MultipartFile image,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        councilService.updateCouncil(request, image, userPrincipal);
        return ResponseEntity.ok(
                ApiResult.builder()
                        .check(true)
                        .message("총학정보가 수정되었습니다.")
                        .build()
        );
    }

    @Operation(summary = "소개이미지 조회")
    @GetMapping("/images")
    public ResponseEntity<ApiResult> getCouncilImages() {
        ApiResult apiResult = ApiResult.builder()
                .check(true)
                .information(councilService.getAllCouncilImages())
                .message("소개이미지를 성공적으로 조회했습니다.")
                .build();
        return ResponseEntity.ok(apiResult);
    }

    @Operation(summary = "소개이미지 등록")
    @PostMapping("/images")
    public ResponseEntity<?> createCouncilImage(
            @RequestPart("description") String description,
            @RequestPart(value = "image") MultipartFile image,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        councilService.createCouncilImage(description, image, userPrincipal);
        return ResponseEntity.ok(
                ApiResult.builder()
                        .check(true)
                        .message("소개이미지가 등록되었습니다.")
                        .build()
        );
    }
    @Operation(summary = "소개이미지 수정")
    @PutMapping("/images/{councilImageId}")
    public ResponseEntity<?> updateCouncilImage(
            @PathVariable Long councilImageId,
            @RequestPart("description") String description,
            @RequestPart(value = "image") MultipartFile image,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        councilService.updateCouncilImage(councilImageId, description, image, userPrincipal);
        return ResponseEntity.ok(
                ApiResult.builder()
                        .check(true)
                        .message("소개이미지가 수정되었습니다.")
                        .build()
        );
    }

    @Operation(summary = "소개이미지 삭제")
    @DeleteMapping("/images/{councilImageId}")
    public ResponseEntity<?> deleteCouncilImage(@PathVariable Long councilImageId,
                                                @AuthenticationPrincipal UserPrincipal userPrincipal) {
        councilService.deleteCouncilImage(councilImageId, userPrincipal);
        return ResponseEntity.ok(
                ApiResult.builder()
                        .check(true)
                        .message("소개이미지가 삭제되었습니다.")
                        .build()
        );
    }
}
