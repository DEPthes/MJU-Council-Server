package depth.mju.council.domain.department.controller;

import depth.mju.council.domain.department.service.DepartmentService;
import depth.mju.council.global.config.UserPrincipal;
import depth.mju.council.global.payload.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentService departmentService;

    @Operation(summary = "국별 소개 조회")
    @GetMapping("")
    public ResponseEntity<ApiResult> getAllDepartments() {
        ApiResult apiResult = ApiResult.builder()
                .check(true)
                .information(departmentService.getAllDepartments())
                .message("국별 소개를 성공적으로 조회했습니다.")
                .build();
        return ResponseEntity.ok(apiResult);
    }

    @Operation(summary = "국별 소개 등록")
    @PostMapping("")
    public ResponseEntity<?> createDepartments(
            @RequestPart("description") String description,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        departmentService.createDepartment(description, image, userPrincipal);
        return ResponseEntity.ok(
                ApiResult.builder()
                        .check(true)
                        .message("국별 소개가 등록되었습니다.")
                        .build()
        );
    }

    @Operation(summary = "국별 소개 수정")
    @PutMapping("/{departmentId}")
    public ResponseEntity<?> updateDepartment(
            @PathVariable Long departmentId,
            @RequestPart("description") String description,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        departmentService.updateDepartment(departmentId, description, image, userPrincipal);
        return ResponseEntity.ok(
                ApiResult.builder()
                        .check(true)
                        .message("국별 소개가 수정되었습니다.")
                        .build()
        );
    }

    @Operation(summary = "국별 소개 삭제")
    @DeleteMapping("/{departmentId}")
    public ResponseEntity<?> deleteDepartment(@PathVariable Long departmentId,
                                              @AuthenticationPrincipal UserPrincipal userPrincipal) {
        departmentService.deleteDepartment(departmentId, userPrincipal);
        return ResponseEntity.ok(
                ApiResult.builder()
                        .check(true)
                        .message("국별 소개가 삭제되었습니다.")
                        .build()
        );
    }
}
