package depth.mju.council.domain.notice.service;

import depth.mju.council.domain.common.FileType;
import depth.mju.council.domain.notice.dto.req.ModifyNoticeReq;
import depth.mju.council.domain.notice.dto.req.CreateNoticeReq;
import depth.mju.council.domain.notice.dto.res.NoticeListRes;
import depth.mju.council.domain.notice.dto.res.NoticeRes;
import depth.mju.council.domain.notice.entity.Notice;
import depth.mju.council.domain.notice.entity.NoticeFile;
import depth.mju.council.domain.notice.repository.NoticeFileRepository;
import depth.mju.council.domain.notice.repository.NoticeRepository;
import depth.mju.council.domain.user.entity.UserEntity;
import depth.mju.council.domain.user.repository.UserRepository;
import depth.mju.council.global.DefaultAssert;
import depth.mju.council.domain.notice.dto.res.FileRes;
import depth.mju.council.global.payload.PageResponse;
import depth.mju.council.infrastructure.s3.service.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeService {

    private final S3Uploader s3Uploader;

    private final UserRepository userRepository;
    private final NoticeRepository noticeRepository;
    private final NoticeFileRepository noticeFileRepository;

    public NoticeRes getNotice(Long noticeId) {
        Notice notice = validNoticeById(noticeId);
        List<FileRes> images = noticeFileRepository.findNoticeFilesByNoticeIdAndFileType(noticeId, FileType.IMAGE);
        List<FileRes> files = noticeFileRepository.findNoticeFilesByNoticeIdAndFileType(noticeId, FileType.FILE);

        return NoticeRes.builder()
                .title(notice.getTitle())
                .content(notice.getContent())
                .createdAt(notice.getCreatedAt().toLocalDate())
                .images(images)
                .files(files)
                .build();
    }

    public PageResponse<NoticeListRes> getAllNotice(Optional<String> keyword, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt")));

        Page<NoticeListRes> noticeListResponses;
        if (keyword.isPresent()) {
            noticeListResponses = noticeRepository.findByTitleContaining(keyword.get(), pageRequest);
        } else {
            noticeListResponses = noticeRepository.findAllNotices(pageRequest);
        }
        return PageResponse.<NoticeListRes>builder()
                .totalElements(noticeListResponses.getTotalElements())
                .totalPage(noticeListResponses.getTotalPages())
                .pageSize(noticeListResponses.getSize())
                .contents(noticeListResponses.getContent())
                .build();
    }

    @Transactional
    public void createNotice(
            List<MultipartFile> images, List<MultipartFile> files, CreateNoticeReq createNoticeReq)
    {
        UserEntity userEntity = userRepository.findById(1L).get(); // 임시

        Notice notice = Notice.builder()
                .title(createNoticeReq.getTitle())
                .content(createNoticeReq.getContent())
                .userEntity(userEntity)
                .build();
        noticeRepository.save(notice);

        uploadNoticeImages(images, notice);
        uploadNoticeFiles(files, notice);

    }

    private void uploadNoticeImages(List<MultipartFile> images, Notice notice) {
        for (MultipartFile image : images) {
            if (!image.isEmpty()) {
                saveNoticeFiles(s3Uploader.uploadImage(image), image.getOriginalFilename(), FileType.IMAGE, notice);
            }
        }
    }

    private void uploadNoticeFiles(List<MultipartFile> files, Notice notice) {
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                saveNoticeFiles(s3Uploader.uploadFile(file), file.getOriginalFilename(), FileType.FILE, notice);
            }
        }
    }

    private void saveNoticeFiles(String fileUrl, String originalFileName, FileType fileType, Notice notice) {
        noticeFileRepository.save(NoticeFile.builder()
                .fileUrl(fileUrl)
                .fileName(originalFileName)
                .fileType(fileType)
                .notice(notice)
                .build());
    }

    @Transactional
    public void deleteNotice(Long noticeId) {
        Notice notice = validNoticeById(noticeId);
        // SOFT DELETE로 구현
        notice.updateIsDeleted(true);
        noticeFileRepository.updateIsDeletedByNoticeId(noticeId, true);
    }

    @Transactional
    public void deleteAllNotice() {
        noticeRepository.updateIsDeletedForAll(true);
        noticeFileRepository.updateIsDeletedForAll(true);
    }

    @Transactional
    public void modifyNotice(Long noticeId, List<MultipartFile> images, List<MultipartFile> files, ModifyNoticeReq modifyNoticeReq) {
        Notice notice = validNoticeById(noticeId);
        // Notice 정보 변경
        notice.updateTitleAndContent(modifyNoticeReq.getTitle(), modifyNoticeReq.getContent());
        // 지우고자 하는 이미지/파일 삭제
        deleteNoticeFiles(modifyNoticeReq.getDeleteFiles());
        deleteNoticeImages(modifyNoticeReq.getDeleteImages());
        // 파일/이미지 업로드
        uploadNoticeImages(images, notice);
        uploadNoticeFiles(files, notice);
    }

    private void deleteNoticeFiles(List<Integer> files) {
        if (files == null || files.isEmpty()) {
            return;
        }
        List<Long> fileIds = files.stream().map(Long::valueOf).collect(Collectors.toList());
        List<NoticeFile> filesToDelete = noticeFileRepository.findAllById(fileIds);
        filesToDelete.forEach(file -> {
            // 저장 파일명 구하기
            String saveFileName = extractSaveFileName(file.getFileUrl());
            // S3에서 삭제
            s3Uploader.deleteFile(saveFileName);
            // DB에서 삭제
            noticeFileRepository.delete(file);
        });
    }

    private void deleteNoticeImages(List<Integer> images) {
        if (images == null || images.isEmpty()) {
            return;
        }
        List<Long> fileIds = images.stream().map(Long::valueOf).collect(Collectors.toList());
        List<NoticeFile> filesToDelete = noticeFileRepository.findAllById(fileIds);
        filesToDelete.forEach(image -> {
            String saveFileName = extractSaveFileName(image.getFileUrl());
            s3Uploader.deleteImage(saveFileName);
            noticeFileRepository.delete(image);
        });
    }

    public String extractSaveFileName(String fileUrl) {
        String[] parts = fileUrl.split("/");
        return parts[parts.length - 1];
    }

    private Notice validNoticeById(Long noticeId) {
        Optional<Notice> noticeOptional = noticeRepository.findByIdAndIsDeleted(noticeId, false);
        DefaultAssert.isOptionalPresent(noticeOptional);
        return noticeOptional.get();
    }

}