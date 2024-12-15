package depth.mju.council.domain.banner.controller;

import depth.mju.council.domain.banner.service.BannerService;
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

@RestController
@RequestMapping("/api/v1/banners")
@RequiredArgsConstructor
public class BannerController {
    private final BannerService bannerService;
    @Operation(summary = "배너 추가 API", description = "배너를 추가하는 API입니다.")
    @PostMapping
    public ResponseEntity<ApiResult> createBanner(
            @Parameter(description = "User의 토큰을 입력해주세요.", required = true) @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestPart(value = "img", required = false) MultipartFile img) {
        bannerService.createBanner(userPrincipal.getId(), img);
        ApiResult result = ApiResult.builder()
                .check(true)
                .information("배너를 추가했어요")
                .build();
        return ResponseEntity.ok(result);
    }
    @Operation(summary = "배너 조회 API", description = "배너 목록을 조회하는 API입니다.")
    @GetMapping
    public ResponseEntity<ApiResult> getBanner() {
        ApiResult result = ApiResult.builder()
                .check(true)
                .information(bannerService.getBanner())
                .build();
        return ResponseEntity.ok(result);
    }
    @Operation(summary = "배너 수정 API", description = "배너 목록을 수정하는 API입니다.")
    @PutMapping("/{bannerId}")
    public ResponseEntity<ApiResult> modifyBanner(
            @Parameter(description = "User의 토큰을 입력해주세요.", required = true) @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long bannerId,
            @RequestPart(value = "img", required = false) MultipartFile img) {
        bannerService.modifyBanner(bannerId, img);
        ApiResult result = ApiResult.builder()
                .check(true)
                .information("배너를 수정했어요")
                .build();
        return ResponseEntity.ok(result);
    }
    @Operation(summary = "배너 삭제 API", description = "배너를 삭제하는 API입니다.")
    @DeleteMapping("/{bannerId}")
    public ResponseEntity<ApiResult> deleteBanner(
            @Parameter(description = "User의 토큰을 입력해주세요.", required = true) @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long bannerId) {
        bannerService.deleteBanner(bannerId);
        ApiResult result = ApiResult.builder()
                .check(true)
                .information("배너를 삭제했어요")
                .build();
        return ResponseEntity.ok(result);
    }
}