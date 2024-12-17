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
import depth.mju.council.global.config.UserPrincipal;
import depth.mju.council.global.payload.PageResponse;
import depth.mju.council.infrastructure.s3.service.S3Service;
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

    private final S3Service s3Service;

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
            UserPrincipal userPrincipal, List<MultipartFile> images, List<MultipartFile> files, CreateNoticeReq createNoticeReq)
    {
        UserEntity user = validUserById(userPrincipal.getId());
        Notice notice = Notice.builder()
                .title(createNoticeReq.getTitle())
                .content(createNoticeReq.getContent())
                .userEntity(user)
                .build();
        noticeRepository.save(notice);

        uploadNoticeFiles(images, notice, FileType.IMAGE);
        uploadNoticeFiles(files, notice, FileType.FILE);
    }

    private void uploadNoticeFiles(List<MultipartFile> files, Notice notice, FileType fileType) {
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                saveNoticeFiles(s3Service.uploadFile(file), file.getOriginalFilename(), fileType, notice);
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
        List<NoticeFile> noticeFiles = noticeFileRepository.findByNotice(notice);
        deleteNoticeFiles(noticeFiles, FileType.FILE);
        deleteNoticeFiles(noticeFiles, FileType.IMAGE);

        noticeRepository.delete(notice);
    }

    @Transactional
    public void deleteAllNotice() {
        noticeRepository.updateIsDeletedForAll(true);
        noticeFileRepository.updateIsDeletedForAll(true);
    }

    @Transactional
    public void modifyNotice(Long noticeId, List<MultipartFile> images, List<MultipartFile> files, ModifyNoticeReq modifyNoticeReq) {
        Notice notice = validNoticeById(noticeId);
        notice.updateTitleAndContent(modifyNoticeReq.getTitle(), modifyNoticeReq.getContent());
        // 지우고자 하는 이미지/파일 삭제
        findNoticeFilesByIds(modifyNoticeReq.getDeleteFiles(), FileType.FILE);
        findNoticeFilesByIds(modifyNoticeReq.getDeleteImages(), FileType.IMAGE);
        // 파일/이미지 업로드
        uploadNoticeFiles(images, notice, FileType.IMAGE);
        uploadNoticeFiles(files, notice, FileType.FILE);
    }

    private void findNoticeFilesByIds(List<Integer> files, FileType fileType) {
        if (files == null || files.isEmpty()) {
            return;
        }
        List<Long> fileIds = files.stream().map(Long::valueOf).collect(Collectors.toList());
        List<NoticeFile> filesToDelete = noticeFileRepository.findAllById(fileIds);
        deleteNoticeFiles(filesToDelete, fileType);
    }

    private void deleteNoticeFiles(List<NoticeFile> files, FileType fileType) {
        files.forEach(file -> {
            // 저장 파일명 구하기
            String saveFileName = s3Service.extractImageNameFromUrl(file.getFileUrl());
            // S3에서 삭제
            if (fileType == FileType.FILE) {
                s3Service.deleteFile(saveFileName);
            } else {
                s3Service.deleteImage(saveFileName);
            }
        });
        noticeFileRepository.deleteNoticeFiles(files);
    }

    private Notice validNoticeById(Long noticeId) {
        Optional<Notice> noticeOptional = noticeRepository.findByIdAndIsDeleted(noticeId, false);
        DefaultAssert.isOptionalPresent(noticeOptional);
        return noticeOptional.get();
    }

    private UserEntity validUserById(Long userId) {
        Optional<UserEntity> userOptional = userRepository.findById(userId);
        DefaultAssert.isOptionalPresent(userOptional);
        return userOptional.get();
    }

}