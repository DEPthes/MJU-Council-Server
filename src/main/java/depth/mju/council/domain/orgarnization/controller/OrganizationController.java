package depth.mju.council.domain.orgarnization.controller;

import depth.mju.council.domain.orgarnization.service.OrganizationService;
import depth.mju.council.global.config.UserPrincipal;
import depth.mju.council.global.payload.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/organizations")
@RequiredArgsConstructor
public class OrganizationController {
    private final OrganizationService organizationService;

    @Operation(summary = "조직도 조회")
    @GetMapping("")
    public ResponseEntity<ApiResult> getAllOrgarnizations() {
        ApiResult apiResult = ApiResult.builder()
                .check(true)
                .information(organizationService.getAllOrganizations())
                .message("조직도를 성공적으로 조회했습니다.")
                .build();
        return ResponseEntity.ok(apiResult);
    }

    @Operation(summary = "조직도 등록")
    @PostMapping("")
    public ResponseEntity<?> createOrganization(
            @RequestPart("titles") String title,
            @RequestPart("images") MultipartFile image,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        organizationService.createOrganization(title, image, userPrincipal);
        return ResponseEntity.ok(
                ApiResult.builder()
                        .check(true)
                        .message("조직도가 등록되었습니다.")
                        .build()
        );
    }

    @Operation(summary = "조직도 수정")
    @PutMapping("/{organizationId}")
    public ResponseEntity<?> updateOrganization(
            @PathVariable Long organizationId,
            @RequestPart("title") String title,
            @RequestPart("image") MultipartFile image,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        organizationService.updateOrganization(organizationId, title, image, userPrincipal);
        return ResponseEntity.ok(
                ApiResult.builder()
                        .check(true)
                        .message("조직도가 수정되었습니다.")
                        .build()
        );
    }
    @Operation(summary = "조직도 삭제")
    @DeleteMapping("/{organizationId}")
    public ResponseEntity<?> deleteOrganization(@PathVariable Long organizationId,
                                                @AuthenticationPrincipal UserPrincipal userPrincipal) {
        organizationService.deleteOrganization(organizationId, userPrincipal);
        return ResponseEntity.ok(
                ApiResult.builder()
                        .check(true)
                        .message("조직도가 삭제되었습니다.")
                        .build()
        );
    }
}
