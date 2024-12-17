package depth.mju.council.domain.notice.controller;

import depth.mju.council.domain.notice.dto.req.ModifyNoticeReq;
import depth.mju.council.domain.notice.dto.req.CreateNoticeReq;
import depth.mju.council.domain.notice.service.NoticeService;
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
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/notices")
@RequiredArgsConstructor
public class NoticeController {
    private final NoticeService noticeService;

    @Operation(summary = "공지사항 상세 조회")
    @GetMapping("/{noticeId}")
    public ResponseEntity<ApiResult> getNotice(
            @Parameter(description = "조회하고자 하는 공지사항의 id를 입력해주세요.", required = true) @PathVariable Long noticeId
    ) {
        ApiResult apiResult = ApiResult.builder()
                .check(true)
                .information(noticeService.getNotice(noticeId))
                .message("공지사항 " + noticeId +"번을 조회합니다.")
                .build();
        return ResponseEntity.ok(apiResult);
    }

    @Operation(summary = "공지사항 목록 조회")
    @GetMapping("")
    public ResponseEntity<ApiResult> getAllNotice(
            @Parameter(description = "현재 페이지의 번호입니다. 0부터 시작합니다.", required = true) @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "한 페이지의 개수입니다.", required = true) @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "검색어입니다. 검색하지 않을 경우, 값을 보내지 않습니다.", required = false) @RequestParam Optional<String> keyword
    ) {
        ApiResult apiResult = ApiResult.builder()
                .check(true)
                .information(noticeService.getAllNotice(keyword, page, size))
                .message("공지사항 목록을 조회합니다.")
                .build();
        return ResponseEntity.ok(apiResult);
    }

    @Operation(summary = "공지사항 등록")
    @PostMapping()
    public ResponseEntity<ApiResult> createNotice(
            @Parameter(description = "User의 토큰을 입력해주세요.", required = true) @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(description = "Multiaprt form-data 형식으로, 업로드할 이미지의 리스트입니다. 보낼 데이터가 없다면 빈 리스트로 전달해주세요.", required = true) @RequestPart List<MultipartFile> images,
            @Parameter(description = "Multiaprt form-data 형식으로, 업로드할 파일의 리스트입니다. 보낼 데이터가 없다면 빈 리스트로 전달해주세요.", required = true) @RequestPart List<MultipartFile> files,
            @Parameter(description = "Schemas의 NoticeRequest를 참고해주세요.", required = true) @Valid @RequestPart CreateNoticeReq createNoticeReq
    ) {
        noticeService.createNotice(userPrincipal, images, files, createNoticeReq);
        ApiResult apiResult = ApiResult.builder()
                .check(true)
                .message("공지사항이 등록되었습니다.")
                .build();
        return ResponseEntity.ok(apiResult);
    }

    @Operation(summary = "공지사항 전체 삭제")
    @DeleteMapping()
    public ResponseEntity<ApiResult> deleteAllNotice(
            @Parameter(description = "User의 토큰을 입력해주세요.", required = true) @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        noticeService.deleteAllNotice();
        ApiResult apiResult = ApiResult.builder()
                .check(true)
                .message("공지사항이 전체 삭제되었습니다.")
                .build();
        return ResponseEntity.ok(apiResult);
    }

    @Operation(summary = "공지사항 삭제")
    @DeleteMapping("/{noticeId}")
    public ResponseEntity<ApiResult> deleteNotice(
            @Parameter(description = "User의 토큰을 입력해주세요.", required = true) @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(description = "삭제하고자 하는 공지사항의 id를 입력해주세요.", required = true) @PathVariable Long noticeId
    ) {
        noticeService.deleteNotice(noticeId);
        ApiResult apiResult = ApiResult.builder()
                .check(true)
                .message("공지사항이 삭제되었습니다.")
                .build();
        return ResponseEntity.ok(apiResult);
    }

    @Operation(summary = "공지사항 수정")
    @PutMapping("/{noticeId}")
    public ResponseEntity<ApiResult> modifyNotice(
            @Parameter(description = "User의 토큰을 입력해주세요.", required = true) @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Parameter(description = "수정하고자 하는 공지사항의 id를 입력해주세요.", required = true) @PathVariable Long noticeId,
            @Parameter(description = "Multiaprt form-data 형식으로, 업로드할 이미지의 리스트입니다. 보낼 데이터가 없다면 빈 리스트로 전달해주세요.", required = true) @RequestPart List<MultipartFile> images,
            @Parameter(description = "Multiaprt form-data 형식으로, 업로드할 파일의 리스트입니다. 보낼 데이터가 없다면 빈 리스트로 전달해주세요.", required = true) @RequestPart List<MultipartFile> files,
            @Parameter(description = "Schemas의 ModifyNoticeRequest를 참고해주세요.", required = true) @Valid @RequestPart ModifyNoticeReq modifyNoticeReq
            ) {
        noticeService.modifyNotice(noticeId, images, files, modifyNoticeReq);
        ApiResult apiResult = ApiResult.builder()
                .check(true)
                .message("공지사항이 수정되었습니다.")
                .build();
        return ResponseEntity.ok(apiResult);
    }


}
